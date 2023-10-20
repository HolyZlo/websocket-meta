package org.example.model;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Builder
@ToString
@Data
public class Message implements Serializable {
    private String from;
    private String to;
    private String content;
}
