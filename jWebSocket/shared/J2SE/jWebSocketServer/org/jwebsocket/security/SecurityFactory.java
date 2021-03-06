//	---------------------------------------------------------------------------
//	jWebSocket - Security Factory
//	Copyright (c) 2010 Alexander Schulze, Innotrade GmbH
//	---------------------------------------------------------------------------
//	This program is free software; you can redistribute it and/or modify it
//	under the terms of the GNU Lesser General Public License as published by the
//	Free Software Foundation; either version 3 of the License, or (at your
//	option) any later version.
//	This program is distributed in the hope that it will be useful, but WITHOUT
//	ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
//	FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for
//	more details.
//	You should have received a copy of the GNU Lesser General Public License along
//	with this program; if not, see <http://www.gnu.org/licenses/lgpl.html>.
//	---------------------------------------------------------------------------
package org.jwebsocket.security;

import java.util.List;
import java.util.Set;

import org.jwebsocket.config.JWebSocketConfig;
import org.jwebsocket.config.xml.RightConfig;
import org.jwebsocket.config.xml.RoleConfig;
import org.jwebsocket.config.xml.UserConfig;

/**
 * implements the security capabilities of jWebSocket.
 * @author aschulze
 */
public class SecurityFactory {

	// private static Logger log = Logging.getLogger(SecurityFactory.class);
	private static Users mUsers = null;
	private static Rights mRights = null;
	private static Roles mRoles = null;

	/**
	 *
	 */
	public static String USER_ANONYMOUS = "guest";
	public static String USER_REG_USER = "user";
	public static String USER_ADMIN = "admin";
	public static String USER_LOCKED = "locked";

	/**
	 * initializes the security system with some default settings to allow to
	 * startup without a config file, this will be removed in the final release!
	 */
	public static void initDefault() {
		/*
		if (log.isDebugEnabled()) {
		log.debug("Initializing demo rights, roles and users...");
		}
		 */
		mRights = new Rights();
		// specify rights
		Right lRPC = new Right("org.jwebsocket.plugins.rpc", "rpc", "Allow Remote Procedure Calls (RPC) to server");
		Right lRRPC = new Right("org.jwebsocket.plugins.rpc", "rrpc", "Allow Reverse Remote Procedure Calls (RRPC) to other clients");
		mRights.addRight(lRPC);
		mRights.addRight(lRRPC);

		// specify roles and assign rights to roles
		// TODO: needs to be removed in final release!
		Role lGuestRole = new Role("guest", "Guests", lRPC, lRRPC);
		Role lRegRole = new Role("regUser", "Registered Users", lRPC, lRRPC);
		Role lAdminRole = new Role("admin", "Administrators", lRPC, lRRPC);

		// specify role sets for a simpler assignment to the users
		Roles lGuestRoles = new Roles(lGuestRole);
		Roles lRegRoles = new Roles(lGuestRole, lRegRole);
		Roles lAdminRoles = new Roles(lGuestRole, lRegRole, lAdminRole);

		User lGuestUser = new User(USER_ANONYMOUS, "Guest", "Guest", "guest", lGuestRoles);
		User lRegUser = new User(USER_REG_USER, "User", "User", "user", lRegRoles);
		User lAdminUser = new User(USER_ADMIN, "Admin", "Admin", "admin", lAdminRoles);
		// add a locked user for test purposes, e.g. to reject token in system filter
		User lLockedUser = new User(USER_LOCKED, "Locked", "Locked", "locked", lGuestRoles);
		lLockedUser.setStatus(User.ST_LOCKED);

		mUsers = new Users();
		mUsers.addUser(lGuestUser);
		mUsers.addUser(lRegUser);
		mUsers.addUser(lAdminUser);
		mUsers.addUser(lLockedUser);

		// log.info("Default rights, roles and users initialized.");
	}

	/**
	 * initializes the security system with the settings from the
	 * jWebSocket.xml.
	 * @param aConfig
	 */
	public static void initFromConfig(JWebSocketConfig aConfig) {

		// build list of rights
		List<RightConfig> lGlobalRights = aConfig.getGlobalRights();
		mRights = new Rights();
		for (RightConfig lRightConfig : lGlobalRights) {
			Right lRight = new Right(
					lRightConfig.getNamespace(),
					lRightConfig.getId(),
					lRightConfig.getDescription());
			mRights.addRight(lRight);
		}

		// build list of roles
		List<RoleConfig> globalRoles = aConfig.getGlobalRoles();
		mRoles = new Roles();
		for (RoleConfig lRoleConfig : globalRoles) {
			Rights lRights = new Rights();
			for (String lRightId : lRoleConfig.getRights()) {
				Right lRight = mRights.get(lRightId);
				if (lRight != null) {
					lRights.addRight(lRight);
				}
			}
			Role lRole = new Role(
					lRoleConfig.getId(),
					lRoleConfig.getDescription(),
					lRights);
			mRoles.addRole(lRole);
		}

		// build list of users
		List<UserConfig> globalUsers = aConfig.getUsers();
		mUsers = new Users();
		for (UserConfig lUserConfig : globalUsers) {
			Roles lRoles = new Roles();
			for (String lRoleId : lUserConfig.getRoles()) {
				Role lRole = mRoles.getRole(lRoleId);
				if (lRole != null) {
					lRoles.addRole(lRole);
				}
			}
			User lUser = new User(
					lUserConfig.getLoginname(),
					lUserConfig.getFirstname(),
					lUserConfig.getLastname(),
					lUserConfig.getPassword(),
					lRoles);

			mUsers.addUser(lUser);
		}

		// log.info("Rights, roles and users successfully initialized.");
	}

	public static void init() {
		// System.out.println(
		//		"JWEBSOCKET_HOME variable not set, using default configuration...");
		// initialize the security factory with some default demo data
		// to show at least something even with no config
		// TODO: only temporary, will be removed in the final release!
		SecurityFactory.initDefault();
	}

	/**
	 * Returns a user by its loginname or <tt>null</tt> if no user with the
	 * given loginname could be found.
	 * @param aLoginname
	 * @return
	 */
	public static User getUser(String aLoginname) {
		// if user is not logged in use configured "anonymous" account
		if (aLoginname == null) {
			aLoginname = SecurityFactory.USER_ANONYMOUS;
		}
		User lUser = mUsers.getUserByLoginName(aLoginname);
		// if the user is not found use the "anonymous" account
		// TODO: this process needs to be changed in the final release!
		if (lUser == null && !SecurityFactory.USER_ANONYMOUS.equals(aLoginname)) {
			aLoginname = SecurityFactory.USER_ANONYMOUS;
			lUser = mUsers.getUserByLoginName(aLoginname);
		}
		return lUser;
	}
	
	public static boolean isValidUser(String aLoginname) {
	    User lUser = mUsers.getUserByLoginName(aLoginname);
	    if (lUser != null) {
	        return true;
	    } else {
	        return false;
	    }
	}
	
	/**
	 * Returns the root user for the jWebSocket system.
	 */
	public static User getRootUser() {
		return  mUsers.getUserByLoginName("root");
	}

	/**
	 * checks if a user identified by its login name has a certain right.
	 * @param aLoginname
	 * @param aRight
	 * @return
	 */
	public static boolean hasRight(String aLoginname, String aRight) {
		boolean lHasRight = false;
		User lUser = getUser(aLoginname);
		if (lUser != null) {
			return lUser.hasRight(aRight);
		}
		return lHasRight;
	}

	/**
	 * checks if a user identified by its login name has a certain role.
	 * @param aLoginname
	 * @param aRole
	 * @return
	 */
	public static boolean hasRole(String aLoginname, String aRole) {
		boolean lHasRole = false;
		// if user is not logged in use configured "anonymous" account
		User lUser = getUser(aLoginname);
		if (lUser != null) {
			return lUser.hasRole(aRole);
		}
		return lHasRole;
	}

	/**
	 * returns an unmodifiable set of role ids for a user instance.
	 * @return
	 */
	public static Set<String> getRoleIdSet(String aUsername) {
		User lUser = getUser(aUsername);
		if( lUser != null ) {
			return lUser.getRoleIdSet();
		}
		return null;
	}

	/**
	 * returns an unmodifiable set of right ids for a given user instance.
	 * @return
	 */
	public static Set<String> getRightIdSet(String aUsername) {
		User lUser = getUser(aUsername);
		if( lUser != null ) {
			return lUser.getRightIdSet();
		}
		return null;
	}

	/**
	 * returns an unmodifiable set of roles for a given user instance.
	 * @return
	 */
	public static Roles getUserRoles(String aUsername) {
		User lUser = getUser(aUsername);
		if( lUser != null ) {
			return lUser.getRoles();
		}
		return null;
	}

	/**
	 * returns an unmodifiable set of global roles.
	 * @return
	 */
	public static Roles getGlobalRoles() {
		return mRoles;
	}

	/**
	 * returns an unmodifiable set of rights for a given user instance.
	 * @return
	 */
	public static Rights getUserRights(String aUsername) {
		User lUser = getUser(aUsername);
		if( lUser != null ) {
			return lUser.getRights();
		}
		return null;
	}

	/**
	 * returns an unmodifiable set of global rights.
	 * @return
	 */
	public static Rights getGlobalRights() {
		return mRights;
	}

	/**
	 * returns an unmodifiable set of rights for this user instance.
	 * @return
	 */
	public static Rights getGlobalRights(String aNamespace) {
		// the getRights method of the Roles class already delivers an
		// unmodifiable set of rights
		Rights lRights = new Rights();
		if (aNamespace != null) {
			for (Right lRight : mRights.getRights()) {
				if (aNamespace == null || lRight.getId().startsWith(aNamespace)) {
					lRights.addRight(lRight);
				}
			}
		}
		return lRights;
	}


}
