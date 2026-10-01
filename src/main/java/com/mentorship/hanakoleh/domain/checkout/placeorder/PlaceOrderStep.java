package com.mentorship.hanakoleh.domain.checkout.placeorder;

public interface PlaceOrderStep {
    void handle(PlaceOrderContext context, PlaceOrderChain chain);
}
