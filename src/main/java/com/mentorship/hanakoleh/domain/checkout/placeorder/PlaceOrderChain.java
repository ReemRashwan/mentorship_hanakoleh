package com.mentorship.hanakoleh.domain.checkout.placeorder;

import java.util.List;

public final class PlaceOrderChain {

    private final List<PlaceOrderStep> steps;
    private int index;

    public PlaceOrderChain(List<PlaceOrderStep> steps) {
        this.steps = steps;
    }

    public void next(PlaceOrderContext context) {
        if (index < steps.size()) {
            PlaceOrderStep step = steps.get(index++);
            step.handle(context, this);
        }
    }
}
