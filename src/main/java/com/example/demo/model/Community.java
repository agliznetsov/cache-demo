package com.example.demo.model;

import static com.example.demo.config.CacheNames.COMMUNITY;
import static com.example.demo.config.CacheNames.COMMUNITY_SUB_COMMUNITIES;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(of = {"id", "name", "description"})
@ToString(of = {"id", "name", "description"})
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = COMMUNITY)
public class Community {
	@Id
	private Integer id;

	private String name;

	private String description;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PARENT_COMM", foreignKey = @ForeignKey(name = "FK_PARENT_COMM"))
	private Community parentCommunity;

	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = COMMUNITY_SUB_COMMUNITIES)
	@OneToMany(mappedBy = "parentCommunity", fetch = FetchType.LAZY)
	private Set<Community> subCommunities = new HashSet<>();

	public void setParentCommunity(Community community) {
		if (community != parentCommunity) {
			if (parentCommunity != null) {
				parentCommunity.getSubCommunities().remove(this);
			}
			parentCommunity = community;
			if (parentCommunity != null) {
				parentCommunity.getSubCommunities().add(this);
			}
		}
	}
}
