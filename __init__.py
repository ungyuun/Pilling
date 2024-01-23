from flask import Flask

def create_app():
    app = Flask(__name__)
    
    from .routes import image
    app.register_blueprint(image.blue_image)
    
    return app