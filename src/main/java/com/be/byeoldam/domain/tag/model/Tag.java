package com.be.byeoldam.domain.tag.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "tag")
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "reference_count", nullable = false)
    private int referenceCount;

    @Column(nullable = false)
    String color;

    @Column(nullable = false)
    String bolderColor;

    @PrePersist
    private void prePersist() {
        this.referenceCount = 0;
        if(this.color == null || this.color.isBlank()) {
            this.color = "#ed9fde";
        }
        if(this.bolderColor == null || this.bolderColor.isBlank()) {
            this.bolderColor = "#ea55cf";
        }
    }

    public static Tag create(String name){
        return new Tag(name);
    }

    public static Tag createTag(String name, String color, String bolderColor) {
        return new Tag(name, color, bolderColor);
    }

    private Tag(String name){
        this.name = name;
    }

    private Tag(String name, String color, String bolderColor) {
        this.name = name;
        this.color = color;
        this.bolderColor = bolderColor;
    }

    public void increment() {
        this.referenceCount += 1;
    }

    public void decrement() {
        this.referenceCount -= 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag = (Tag) o;
        return Objects.equals(id, tag.id);  // id를 기준으로 비교
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);  // id를 기준으로 hashCode 생성
    }
}
