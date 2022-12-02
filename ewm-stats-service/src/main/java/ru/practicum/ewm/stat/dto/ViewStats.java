package ru.practicum.ewm.stat.dto;


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
