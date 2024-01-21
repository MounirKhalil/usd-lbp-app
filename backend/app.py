from datetime import datetime, timedelta
from flask import Flask, request, jsonify, abort
from flask_bcrypt import Bcrypt
from flask_sqlalchemy import SQLAlchemy
from sqlalchemy import func
from flask_marshmallow import Marshmallow
from flask_cors import CORS
import jwt
from .db_config import DB_CONFIG
from sqlalchemy import and_, not_

app = Flask(__name__)
app.config['SQLALCHEMY_DATABASE_URI'] = DB_CONFIG
SECRET_KEY = "b'|\xe7\xbfU3`\xc4\xec\xa7\xa9zf:}\xb5\xc7\xb9\x139^3@Dv'"
CORS(app)
db = SQLAlchemy(app)
ma = Marshmallow(app)
bcrypt = Bcrypt(app)

from .model.user import User, user_schema
from .model.transaction import Transaction, transaction_schema, transactions_schema
from .model.exchanges import Exchanges, exchangesSchema, exchange_schema, exchanges_schema


@app.route('/transaction', methods=['POST'])
def create_transaction():
    usd, lbp, usd_lbp = request.json.get('usd_amount'), request.json.get('lbp_amount'), request.json.get('usd_to_lbp')
    auth_token = extract_auth_token(request)
    user_id = decode_token(auth_token) if auth_token else None
    new_transaction = Transaction(usd_amount=usd, lbp_amount=lbp, usd_to_lbp=usd_lbp,
                                  user_id=user_id) if user_id else Transaction(usd_amount=usd, lbp_amount=lbp,
                                                                               usd_to_lbp=usd_lbp)
    db.session.add(new_transaction)
    db.session.commit()
    response = {'message': 'Success', 'transaction': transaction_schema.dump(new_transaction)}
    return jsonify(response)


@app.route('/addrequest', methods=['POST'])
def add_request():
    usd, lbp, usd_lbp = request.json.get('usd_amount'), request.json.get('lbp_amount'), request.json.get('usd_to_lbp')
    auth_token = extract_auth_token(request)
    user_id = decode_token(auth_token) if auth_token else None
    new_exchange = Exchanges(usd_amount=usd, lbp_amount=lbp, usd_to_lbp=usd_lbp, user_id=user_id, accepted=False,
                             receiver_id=None)
    db.session.add(new_exchange)
    db.session.commit()
    response = {'message': 'Success', 'exchange': exchange_schema.dump(new_exchange)}
    return jsonify(response)


@app.route('/transaction', methods=['GET'])
def get_transactions():
    auth_token = extract_auth_token(request)
    user_id = decode_token(auth_token) if auth_token else None
    if not user_id:
        return jsonify({'message': 'Unauthorized'}), 403
    transactions = Transaction.query.filter_by(user_id=user_id).all()
    response = {'transactions': transactions_schema.dump(transactions)}
    return jsonify(response)


@app.route('/getuserrequests', methods=['GET'])
def get_user_requests():
    auth_token = extract_auth_token(request)
    user_id = decode_token(auth_token) if auth_token else None
    if not user_id:
        return jsonify({'message': 'Unauthorized'}), 403
    requests = Exchanges.query.filter(and_((Exchanges.user_id == user_id), Exchanges.accepted == False)).all()
    response = {'requests': exchanges_schema.dump(requests)}
    return jsonify(response)


@app.route('/getotherrequests', methods=['GET'])
def get_other_requests():
    auth_token = extract_auth_token(request)
    user_id = decode_token(auth_token) if auth_token else None
    if not user_id:
        return jsonify({'message': 'Unauthorized'}), 403
    requests = Exchanges.query.filter(and_(not_(Exchanges.user_id == user_id), Exchanges.accepted == False)).all()
    response = {'requests': exchanges_schema.dump(requests)}
    return jsonify(response)


@app.route('/getaccepteduserrequests', methods=['GET'])
def get_accepted_user_requests():
    auth_token = extract_auth_token(request)
    user_id = decode_token(auth_token) if auth_token else None
    if not user_id:
        return jsonify({'message': 'Unauthorized'}), 403
    requests = Exchanges.query.filter(and_((Exchanges.user_id == user_id), Exchanges.accepted == True)).all()
    response = {'requests': exchanges_schema.dump(requests)}
    return jsonify(response)


@app.route('/getacceptedotherrequests', methods=['GET'])
def get_accepted_other_requests():
    auth_token = extract_auth_token(request)
    user_id = decode_token(auth_token) if auth_token else None
    if not user_id:
        return jsonify({'message': 'Unauthorized'}), 403
    requests = Exchanges.query.filter_by(receiver_id=user_id).all()
    response = {'requests': exchanges_schema.dump(requests)}
    return jsonify(response)

@app.route('/acceptexchange', methods=['PUT'])
def accept_exchange():
    id = request.json.get('id')
    auth_token = extract_auth_token(request)
    user_id = decode_token(auth_token) if auth_token else None
    exchange = Exchanges.query.filter_by(id=id).first()
    if not exchange:
        return jsonify({'message': 'Request not found'}), 404
    exchange.accepted = True
    exchange.receiver_id = user_id
    db.session.commit()
    return jsonify({'message': 'Exchange accepted.'}), 200

@app.route('/exchangeRate', methods=['GET'])
def exchange_rate():
    start_date = datetime.now() - timedelta(hours=72)
    usd_lbp_tran = Transaction.query.filter(
        Transaction.added_date.between(start_date, datetime.now()),
        Transaction.usd_to_lbp == 1,
        Transaction.usd_amount > 0
    ).all()
    lbp_usd_tran = Transaction.query.filter(
        Transaction.added_date.between(start_date, datetime.now()),
        Transaction.usd_to_lbp == 0,
        Transaction.usd_amount > 0
    ).all()

    usd_lbp_count = len(usd_lbp_tran)
    lbp_usd_count = len(lbp_usd_tran)
    usd_lbp_total = sum([transaction.lbp_amount / transaction.usd_amount for transaction in usd_lbp_tran])
    lbp_usd_total = sum([transaction.lbp_amount / transaction.usd_amount for transaction in lbp_usd_tran])

    usd_lbp_av = usd_lbp_total / usd_lbp_count if usd_lbp_count else "Not Yet Available"
    lbp_usd_av = lbp_usd_total / lbp_usd_count if lbp_usd_count else "Not Yet Available"

    exchange_rates = {
        "usd_to_lbp": usd_lbp_av,
        "lbp_to_usd": lbp_usd_av
    }

    return jsonify(exchange_rates)


@app.route('/graph_sell', methods=['GET'])
def graph_sell_exchange_rate():
    data = []
    i = 1

    start_date = datetime.now() - timedelta(hours=72)
    usd_lbp_tran = Transaction.query.filter(
        Transaction.added_date.between(start_date, datetime.now()),
        Transaction.usd_to_lbp == 1,
        Transaction.usd_amount > 0
    ).all()

    for transaction in usd_lbp_tran:
        data.append({
            'Sell usd': transaction.lbp_amount / transaction.usd_amount,
            'Number': "Transaction " + str(i),
            'Date': transaction.added_date
        })
        i = i + 1

    return jsonify(data)


@app.route('/graph_buy', methods=['GET'])
def graph_buy_exchange_rate():
    data = []
    i = 1

    start_date = datetime.now() - timedelta(hours=72)
    lbp_usd_tran = Transaction.query.filter(
        Transaction.added_date.between(start_date, datetime.now()),
        Transaction.usd_to_lbp == 0,
        Transaction.usd_amount > 0
    ).all()

    for transaction in lbp_usd_tran:
        data.append({
            'Buy usd': transaction.lbp_amount / transaction.usd_amount,
            'Number': "Transaction " + str(i),
            'Date': transaction.added_date
        })
        i = i + 1

    return jsonify(data)


@app.route('/graph_overallsell', methods=['GET'])
def graph_overallsell_exchange_rate():
    data = []
    i = 1
    add = 0

    start_date = datetime.now() - timedelta(hours=72)
    usd_lbp_tran = Transaction.query.filter(
        Transaction.added_date.between(start_date, datetime.now()),
        Transaction.usd_to_lbp == 1,
        Transaction.usd_amount > 0
    ).all()

    for transaction in usd_lbp_tran:
        add = add + transaction.lbp_amount / transaction.usd_amount
        data.append({
            'Overall Sell usd': add / i,
            'Date': transaction.added_date,
            'Number': "Transaction " + str(i)
        })
        i = i + 1

    return jsonify(data)


@app.route('/graph_overallbuy', methods=['GET'])
def graph_overallbuy_exchange_rate():
    data = []
    i = 1
    add = 0

    start_date = datetime.now() - timedelta(hours=72)
    lbp_usd_tran = Transaction.query.filter(
        Transaction.added_date.between(start_date, datetime.now()),
        Transaction.usd_to_lbp == 0,
        Transaction.usd_amount > 0
    ).all()

    for transaction in lbp_usd_tran:
        add = add + transaction.lbp_amount / transaction.usd_amount
        data.append({
            'Overall Buy usd': add / i,
            'Date': transaction.added_date,
            'Number': "Transaction " + str(i)
        })
        i = i + 1

    return jsonify(data)


@app.route('/user', methods=['POST'])
def create_user():
    user_name = request.json['user_name']
    password = request.json['password']

    existing_user = User.query.filter_by(user_name=user_name).first()
    if existing_user:
        return jsonify({'message': 'User already exists'}), 400

    new_user = User(user_name=user_name, password=password)
    db.session.add(new_user)
    db.session.commit()

    response = {'message': 'Success', 'user': user_schema.dump(new_user)}
    return jsonify(response)


@app.route('/authentication', methods=['POST'])
def auth_user():
    user_name = request.json.get('user_name')
    password = request.json.get('password')

    if not all([user_name, password]):
        return jsonify({'message': 'Missing required fields.'}), 400

    user = User.query.filter_by(user_name=user_name).first()
    if not user:
        return jsonify({'message': 'User not found.'}), 403

    if bcrypt.check_password_hash(user.hashed_password, password):
        token = create_token(user.id)
        return jsonify({'token': token}), 200
    else:
        return jsonify({'message': 'Invalid credentials.'}), 403


def create_token(user_id):
    time_now = datetime.utcnow()
    payload = {
        'exp': time_now + timedelta(days=4),
        'iat': time_now,
        'sub': user_id
    }
    return jwt.encode(
        payload,
        SECRET_KEY,
        algorithm='HS256'
    )


def extract_auth_token(authenticated_request):
    auth_header = authenticated_request.headers.get('Authorization')
    if auth_header:
        return auth_header.split(" ")[1]
    else:
        return None


def decode_token(token):
    payload = jwt.decode(token, SECRET_KEY, 'HS256')
    return payload['sub']


if __name__ == '__main__':
    with app.app_context():
        db.create_all()
    app.run()
