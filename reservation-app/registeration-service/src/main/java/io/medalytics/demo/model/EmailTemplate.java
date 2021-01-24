package io.medalytics.demo.model;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class EmailTemplate {
    private String from;
    private String to;
    private String subject;
    private String text;
}
