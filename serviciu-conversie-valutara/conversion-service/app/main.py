import os
from decimal import Decimal, ROUND_HALF_UP

import httpx
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel, Field

app = FastAPI(
    title="Conversion Service",
    description="Calculează conversii valutare folosind Exchange Rate Service.",
    version="1.0.0",
)

RATE_SERVICE_URL = os.getenv("RATE_SERVICE_URL", "http://localhost:8002")

class ConversionRequest(BaseModel):
    amount: float = Field(gt=0, description="Suma care trebuie convertită")
    from_currency: str = Field(min_length=3, max_length=3)
    to_currency: str = Field(min_length=3, max_length=3)

@app.get("/health")
def health():
    return {"service": "conversion-service", "status": "ok"}

@app.post("/convert")
async def convert(request: ConversionRequest):
    source = request.from_currency.upper()
    target = request.to_currency.upper()

    try:
        async with httpx.AsyncClient(timeout=5.0) as client:
            response = await client.get(f"{RATE_SERVICE_URL}/rates")
            response.raise_for_status()
            rates = response.json()["rates"]
    except (httpx.HTTPError, KeyError, ValueError):
        raise HTTPException(status_code=503, detail="Serviciul de curs valutar nu este disponibil")

    if source not in rates or target not in rates:
        raise HTTPException(status_code=400, detail="Monedă nesuportată")

    amount = Decimal(str(request.amount))
    source_rate = Decimal(str(rates[source]))
    target_rate = Decimal(str(rates[target]))
    result = (amount * target_rate / source_rate).quantize(
        Decimal("0.01"), rounding=ROUND_HALF_UP
    )

    return {
        "amount": float(amount),
        "from_currency": source,
        "to_currency": target,
        "rate": float(target_rate / source_rate),
        "converted_amount": float(result),
    }
