from fastapi import FastAPI

app = FastAPI(
    title="Exchange Rate Service",
    description="Serviciu distribuit care furnizează cursuri valutare demonstrative.",
    version="1.0.0",
)

# Numărul de unități valutare pentru 1 EUR. Date strict demonstrative.
RATES = {
    "EUR": 1.0,
    "RON": 4.97,
    "USD": 1.08,
    "GBP": 0.85,
    "CHF": 0.96,
}

@app.get("/health")
def health():
    return {"service": "rate-service", "status": "ok"}

@app.get("/rates")
def get_rates():
    return {"base": "EUR", "rates": RATES, "source": "demo-static"}
