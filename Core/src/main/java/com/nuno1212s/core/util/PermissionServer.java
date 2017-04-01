package com.nuno1212s.core.util;

import java.util.UUID;

/**
 * Created by COMP on 29/07/2016.
 */
public interface PermissionServer {

	void setServerGroup(UUID player, short groupId);

	short getGroupId(UUID player);

	void handlePlayerGroupChange(UUID player);
}
