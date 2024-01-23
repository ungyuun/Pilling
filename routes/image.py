from flask import Blueprint

blue_image = Blueprint('name',__name__,url_prefix='/hi')

@blue_image.route('/hi')
def hi():
    return "hi~~!~!"

@blue_image.route('/a')
def hello():
    return "헤이 "