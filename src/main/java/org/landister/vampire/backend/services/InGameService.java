package org.landister.vampire.backend.services;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;

import org.bson.types.ObjectId;
import org.jboss.logging.Logger;
import org.landister.vampire.backend.model.dao.game.Game;
import org.landister.vampire.backend.model.dao.game.inner.User;
import org.landister.vampire.backend.util.exceptions.NotFoundException;

/**
 * Used for retrieving ingame specific information
 */
@ApplicationScoped
public class InGameService extends GameService {


  private static final Logger LOG = Logger.getLogger(InGameService.class);


}
