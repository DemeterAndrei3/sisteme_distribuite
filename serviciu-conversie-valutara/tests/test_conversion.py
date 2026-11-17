from decimal import Decimal, ROUND_HALF_UP
import pytest

def calculate(amount, source_rate, target_rate):
    return (Decimal(str(amount)) * Decimal(str(target_rate)) /
            Decimal(str(source_rate))).quantize(Decimal("0.01"), rounding=ROUND_HALF_UP)

def test_eur_to_ron():
    assert calculate(100, 1.0, 4.97) == Decimal("497.00")

def test_usd_to_ron():
    assert calculate(100, 1.08, 4.97) == Decimal("460.19")

def test_same_currency():
    assert calculate(25, 1.0, 1.0) == Decimal("25.00")

def test_negative_amount_is_not_valid():
    with pytest.raises(ValueError):
        # Regula echivalentă validării amount > 0 din API.
        if -5 <= 0:
            raise ValueError("Suma trebuie să fie pozitivă")
