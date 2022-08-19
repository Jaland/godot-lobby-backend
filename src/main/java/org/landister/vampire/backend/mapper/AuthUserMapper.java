package org.landister.vampire.backend.mapper;

import org.landister.vampire.backend.model.dao.auth.AuthUser;
import org.landister.vampire.backend.model.dao.game.inner.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "cdi")
public interface AuthUserMapper {
 
  @Mapping(source = "username", target = "name")
  User toUser(AuthUser user);
  
}
