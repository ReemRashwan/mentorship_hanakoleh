package com.mentorship.hanakoleh.controller.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseMessage {
    private String messageTitle;
    private String messageDetails;
    private LocalDateTime timestamp;


}
