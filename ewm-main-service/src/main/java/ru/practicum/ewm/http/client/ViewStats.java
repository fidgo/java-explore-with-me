package ru.practicum.ewm.http.client;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ViewStats {

    private String app;

    private String uri;

    private Long hits;
}
