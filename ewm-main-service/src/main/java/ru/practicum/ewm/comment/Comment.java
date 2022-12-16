package ru.practicum.ewm.comment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "creator_id")
    private User creator;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    @Column(name = "status", length = 20)
    @Enumerated(EnumType.STRING)
    private StatusComment status;

    @Column(name = "text", length = 5000)
    private String text;

    @Column(name = "created", nullable = false)
    private LocalDateTime created;
}
