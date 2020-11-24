package com.dub.spring.persistence;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.dub.spring.photo.Photo;
import com.dub.spring.user.User;
import com.dub.spring.user.UserEntity;



public interface UserRepository extends CrudRepository<UserEntity, Long>{

	List<UserEntity> findByUsername(String username);
	//List<Photo> findPhotosByShared(boolean shared);
	
	
}
