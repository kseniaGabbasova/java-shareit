package ru.practicum.shareit.booking;

public enum Status {
    WAITING,
    APPROVED,
    REJECTED,
    CANCELLED;

    public static Status getActualStatus(Boolean isApproved, Boolean isCanceled) {
        if (!isApproved && !isCanceled) {
            return WAITING;
        } else if (isApproved && !isCanceled) {
            return APPROVED;
        } else if (!isApproved) {
            return REJECTED;
        } else return REJECTED;
    }
    }
