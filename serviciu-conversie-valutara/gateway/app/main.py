import os
import httpx
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel, Field

app = FastAPI(
    title="Currency Converter API Gateway",
    description="Punct unic de acces către serviciile de conversie valutară.",
    version="1.0.0",
)

CONVERSION_SERVICE_URL = os.getenv("CONVERSION_SERVICE_URL", "http://localhost:8001")
RATE_SERVICE_URL = os.getenv("RATE_SERVICE_URL", "http://localhost:8002")

class ConversionRequest(BaseModel):
    amount: float = Field(gt=0)
    from_currency: str = Field(min_length=3, max_length=3)
    to_currency: str = Field(min_length=3, max_length=3)

@app.get("/")
def root():
    return {"message": "Currency Converter API", "docs": "/docs"}

@app.get("/health")
def health():
    return {"service": "api-gateway", "status": "ok"}

@app.get("/rates")
async def rates():
    try:
        async with httpx.AsyncClient(timeout=5.0) as client:
            response = await client.get(f"{RATE_SERVICE_URL}/rates")
            response.raise_for_status()
            return response.json()
    except httpx.HTTPError:
        raise HTTPException(status_code=503, detail="Serviciul de curs valutar nu este disponibil")

@app.post("/convert")
async def convert(request: ConversionRequest):
    try:
        async with httpx.AsyncClient(timeout=8.0) as client:
            response = await client.post(
                f"{CONVERSION_SERVICE_URL}/convert",
                json=request.model_dump(),
            )
            response.raise_for_status()
            return response.json()
    except httpx.HTTPStatusError as exc:
        # Păstrăm codul și mesajul de eroare trimise de serviciul intern.
        raise HTTPException(
            status_code=exc.response.status_code,
            detail=exc.response.json().get("detail", "Eroare la conversie"),
        )
    except httpx.HTTPError:
        raise HTTPException(status_code=503, detail="Serviciul de conversie nu este disponibil")
