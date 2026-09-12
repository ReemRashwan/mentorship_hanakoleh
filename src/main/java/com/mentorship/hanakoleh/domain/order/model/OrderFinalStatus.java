package com.mentorship.hanakoleh.domain.order.model;

import java.util.Optional;

public enum OrderFinalStatus {
    CREATED {
        @Override
        public boolean canTransitionTo(OrderFinalStatus nextState) {
            return nextState == CONFIRMED || nextState == CANCELLED;
        }

    },

    CONFIRMED {
        @Override
        public boolean canTransitionTo(OrderFinalStatus nextState) {
            return nextState == IN_PROGRESS || nextState == CANCELLED;
        }

    },
    IN_PROGRESS {
        @Override
        public boolean canTransitionTo(OrderFinalStatus nextState) {
            return nextState == READY_FOR_PICKUP || nextState == CANCELLED;
        }

    },
    READY_FOR_PICKUP {
        @Override
        public boolean canTransitionTo(OrderFinalStatus nextState) {
            return nextState == IN_DELIVERY || nextState == CANCELLED;
        }

    },
    IN_DELIVERY {
        @Override
        public boolean canTransitionTo(OrderFinalStatus nextState) {
            return nextState == COMPLETED || nextState == CANCELLED;
        }
    },
    COMPLETED {
        @Override
        public boolean canTransitionTo(OrderFinalStatus nextState) {
            return false;
        }

    },
    CANCELLED {
        @Override
        public boolean canTransitionTo(OrderFinalStatus nextState) {
            return nextState == REFUNDED;
        }

    },
    REFUNDED {
        @Override
        public boolean canTransitionTo(OrderFinalStatus nextState) {
            return false;
        }
    };

    public abstract boolean canTransitionTo(OrderFinalStatus nextState);
}
