from datetime import datetime
from ..app import db, ma


class Exchanges(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    usd_amount = db.Column(db.Float, nullable=False)
    lbp_amount = db.Column(db.Float, nullable=False)
    usd_to_lbp = db.Column(db.Boolean, nullable=False)
    added_date = db.Column(db.DateTime)
    user_id = db.Column(db.Integer, db.ForeignKey('user.id'), nullable=True)
    accepted = db.Column(db.Boolean, nullable=False)
    receiver_id = db.Column(db.Integer, nullable=True)

    def __init__(self, usd_amount, lbp_amount, usd_to_lbp, user_id=None, receiver_id=None, accepted=False):
        super(Exchanges, self).__init__(usd_amount=usd_amount,
                                        lbp_amount=lbp_amount,
                                        usd_to_lbp=usd_to_lbp,
                                        user_id=user_id,
                                        added_date=datetime.now(),
                                        accepted=accepted,
                                        receiver_id=receiver_id
                                        )

    def repr(self):
        return '<exchanges %r>' % self.id


class exchangesSchema(ma.Schema):
    class Meta:
        fields = ("id", "usd_amount", "lbp_amount", "usd_to_lbp", "user_id", "added_date", "accepted", "receiver_id")
        model = Exchanges


exchange_schema = exchangesSchema()
exchanges_schema = exchangesSchema(many=True)
