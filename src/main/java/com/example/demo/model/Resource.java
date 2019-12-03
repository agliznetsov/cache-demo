package com.example.demo.model;

import static com.example.demo.config.CacheNames.RESOURCE;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.example.demo.config.CacheNames;

import lombok.Data;

@Data
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = RESOURCE)
public class Resource {
	@Id
	private Integer id;

	private String name;

	private String description;
}
