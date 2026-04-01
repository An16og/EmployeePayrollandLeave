package com.example.demo.dto;

public class LeaveActionRequest {
    // Optional comment when approving or rejecting a leave
    private String comment;

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}
