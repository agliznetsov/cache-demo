package com.example.demo.service;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dao.CommunityRepository;
import com.example.demo.model.Community;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Transactional
public class CommunityService {
	@Autowired
	CommunityRepository communityRepository;

	public void createTree() {
		int id = 1000;
		for (int i = 0; i < 10; i++) {
			String parentName = "parent" + i;
			Community parent = newCommunity(i, null, parentName);
			communityRepository.saveAndFlush(parent);
			for (int j = 0; j < 10; j++) {
				Community child = newCommunity(id++, parent, parentName + "child" + j);
				communityRepository.save(child);
			}
		}
	}

	public void createCommunity(int id, Integer parentId, String name) {
		Community parent = parentId == null ? null : getCommunity(parentId);
		Community community = newCommunity(id, parent, name);
		communityRepository.saveAndFlush(community);
	}

	public void moveCommunity(int id, Integer parentId) {
		Community community = getCommunity(id);
		Community parent = parentId == null ? null : getCommunity(parentId);
		community.setParentCommunity(parent);
	}

	public void moveCommunityAndFail(int id, Integer parentId) {
		Community community = getCommunity(id);
		Community parent = parentId == null ? null : getCommunity(parentId);
		community.setParentCommunity(parent);
		throw new RuntimeException("error");
	}

	private Community newCommunity(int id, Community parent, String name) {
		Community community = new Community();
		community.setId(id);
		community.setParentCommunity(parent);
		community.setName(name != null ? name : "name" + id);
		return community;
	}

	public void updateCommunity(int id, String newName) {
		Optional<Community> optional = findById(id);
		if (optional.isPresent()) {
			optional.get().setName(newName);
		} else {
			Community asset = new Community();
			asset.setId(id);
			asset.setName(newName);
			communityRepository.saveAndFlush(asset);
		}
	}

	public Community getCommunity(int id) {
		Community community = communityRepository.findById(id).orElseThrow(() -> new RuntimeException("not found"));
		community.getSubCommunities().forEach(it -> it.toString());
		return community;
	}

	public void deleteAll() {
		communityRepository.deleteAll();
	}

	public Optional<Community> findById(int id) {
		return communityRepository.findById(id);
	}

	public List<Community> findAll() {
		return communityRepository.findAll();
	}

	public List<Community> getSubCommunities(int id) {
		return new LinkedList<>(getCommunity(id).getSubCommunities());
	}

}
