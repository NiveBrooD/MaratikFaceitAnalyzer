package com.ramis.dbsaver.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Stats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "games_played")
    private Integer gamesPlayed;

    private Integer wins;

    private Integer loss;

    @Column(name = "avg_kd")
    private Double avgKd;

    @Column(name = "avg_adr")
    private Double avgAdr;

    @Column(name = "avg_kills")
    private Double avgKills;

    @Column(name = "avg_deaths")
    private Double avgDeaths;

    @Column(name = "avg_assists")
    private Double avgAssists;

    @Column(name = "avg_headshots")
    private Double avgHeadshots;

    @Column(name = "avg_double_kills")
    private Double avgDoubleKills;

    @Column(name = "avg_triple_kills")
    private Double avgTripleKills;

    @Column(name = "avg_quadro_kills")
    private Double avgQuadroKills;

    @Column(name = "avg_penta_kills")
    private Double avgPentaKills;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "stats_matches",
            joinColumns = @JoinColumn(name = "stats_id")
    )
    @Column(name = "match_id")
    @OrderColumn(name = "matches_order")
    private List<String> matches = new ArrayList<>();
}
