package com.bit.shared.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(schema = "users", name = "_roles")
public class Role{
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(unique = true, name = "id")
  private Long id;

  @Column(name = "role_name")
  private String name;
}
