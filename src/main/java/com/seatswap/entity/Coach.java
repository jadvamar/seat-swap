package com.seatswap.entity;

import jakarta.persistence.*;

@Entity
@Table(
        name = "coaches",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_journey_coach",
                        columnNames = {"journey_id", "coach_number"}
                )
        }
)
public class Coach {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "journey_id", nullable = false)
    private Journey journey;

    @Column(name = "coach_number", nullable = false)
    private String coachNumber;

    @Column(name = "coach_type", nullable = false)
    private String coachType;

    public Coach() {
    }

    public Long getId() {
        return id;
    }

    public Journey getJourney() {
        return journey;
    }

    public void setJourney(Journey journey) {
        this.journey = journey;
    }

    public String getCoachNumber() {
        return coachNumber;
    }

    public void setCoachNumber(String coachNumber) {
        this.coachNumber = coachNumber;
    }

    public String getCoachType() {
        return coachType;
    }

    public void setCoachType(String coachType) {
        this.coachType = coachType;
    }
}