package ru.practicum.ewm.stat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "statistics")
public class Stat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "app", length = 256, nullable = false)
    private String app;

    @Column(name = "uri", length = 3000, nullable = false)
    private String uri;

    @Column(name = "ip", length = 256, nullable = false)
    private String ip;

    @Column(name = "date_create", nullable = false)
    private LocalDateTime timestamp;
}
