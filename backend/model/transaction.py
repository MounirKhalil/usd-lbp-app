from datetime import datetime
from flask import Flask, request, jsonify, abort
from flask_sqlalchemy import SQLAlchemy
from flask_cors import CORS
from flask_marshmallow import Marshmallow
from flask_bcrypt import Bcrypt
import jwt
from ..app import db, ma, bcrypt

class Transaction(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    usd_amount = db.Column(db.Float, nullable=False)
    lbp_amount = db.Column(db.Float, nullable=False)
    usd_to_lbp = db.Column(db.Boolean, nullable=False)
    added_date = db.Column(db.DateTime)
    user_id = db.Column(db.Integer, db.ForeignKey('user.id'), nullable=True)

    def __init__(self, usd_amount, lbp_amount, usd_to_lbp, user_id = None):
        super(Transaction, self).__init__(usd_amount=usd_amount,
                                          lbp_amount=lbp_amount, usd_to_lbp=usd_to_lbp,
                                          user_id=user_id,
                                          added_date=datetime.now())

    def repr(self):
        return '<Transaction %r>' % self.id

class TransactionSchema(ma.Schema):
    class Meta:
        fields = ("id", "usd_amount", "lbp_amount", "usd_to_lbp", "user_id", "added_date")
        model = Transaction

transaction_schema = TransactionSchema()
transactions_schema = TransactionSchema(many=True)