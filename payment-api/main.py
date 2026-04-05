from fastapi import FastAPI
from config.db import Base, engine
from routes import payment, user

Base.metadata.create_all(bind=engine)

app = FastAPI(
    title="Payment API",
    description="API de simulação de pagamentos para e-commerce",
    version="1.0.0"
)

app.include_router(user.router)
app.include_router(payment.router)
