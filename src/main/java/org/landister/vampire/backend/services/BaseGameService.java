package org.landister.vampire.backend.services;

import javax.enterprise.context.ApplicationScoped;

import org.jboss.logging.Logger;

/**
 * Used if you only need access to the game service
 */
@ApplicationScoped
public class BaseGameService extends GameService {


  private static final Logger LOG = Logger.getLogger(BaseGameService.class);

}
