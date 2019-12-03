package com.example.demo.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Community;

public interface CommunityRepository extends JpaRepository<Community,Integer> {
}
