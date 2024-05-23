package lu.itrust.business.ts.model.general;


/**
 * The LogAction enum represents the different actions that can be logged.
 * Each action corresponds to a specific operation or event in the system.
 * 
 * Possible actions include:
 * - ADD: Adding a new item
 * - ACCESS: Accessing a resource
 * - ACCEPT_ACCESS_REQUEST: Accepting an access request
 * - ACCESS_REQUEST: Requesting access to a resource
 * - APPLY: Applying changes
 * - ARCHIVE: Archiving an item
 * - AUTHENTICATE: Authenticating a user
 * - AUTO_GRANT: Automatically granting access
 * - CANCEL_ACCESS_REQUEST: Canceling an access request
 * - CHANGE: Changing something
 * - CREATE: Creating a new item
 * - CREATE_OR_UPDATE: Creating or updating an item
 * - DELETE: Deleting an item
 * - DENY_ACCESS: Denying access to a resource
 * - DOWNLOAD: Downloading a file
 * - EDIT: Editing an item
 * - EXECUTE: Executing a command or action
 * - EXPORT: Exporting data
 * - GIVE_ACCESS: Giving access to a resource
 * - GRANT_ACCESS: Granting access to a resource
 * - IMPORT: Importing data
 * - INSTALL: Installing something
 * - LOCK_ACCOUNT: Locking an account
 * - MANAGE: Managing something
 * - MOVE: Moving an item
 * - OPEN: Opening a resource
 * - RE_INSTALL: Reinstalling something
 * - RE_START: Restarting something
 * - READ: Reading data or information
 * - REFRESH: Refreshing data or information
 * - REMOVE_ACCESS: Removing access to a resource
 * - RENAME: Renaming an item
 * - RENEW: Renewing something
 * - REJECT_ACCESS_REQUEST: Rejecting an access request
 * - REQUEST_TO_RESET_PASSWORD: Requesting a password reset
 * - RESET_PASSWORD: Resetting a password
 * - RESTORE_ACCESS_RIGHT: Restoring access rights
 * - RESUME: Resuming an operation or activity
 * - RISE_EXCEPTION: Raising an exception
 * - RUN: Running a process or program
 * - SIGN_IN: Signing in to a system or application
 * - SIGN_OUT: Signing out of a system or application
 * - SIGN_UP: Signing up for a service or application
 * - START: Starting an operation or activity
 * - STOP: Stopping an operation or activity
 * - SUSPEND: Suspending an operation or activity
 * - SWITCH_CUSTOMER: Switching to a different customer
 * - SWITCH_OWNER: Switching to a different owner
 * - UPDATE: Updating an item or information
 * - UPLOAD: Uploading a file or data
 */
public enum LogAction {
	ADD,ACCESS, ACCEPT_ACCESS_REQUEST, ACCESS_REQUEST, APPLY, ARCHIVE, AUTHENTICATE, AUTO_GRANT, CANCEL_ACCESS_REQUEST, CHANGE, CREATE, CREATE_OR_UPDATE, DELETE, DENY_ACCESS, DOWNLOAD, EDIT, EXECUTE, EXPORT, GIVE_ACCESS, GRANT_ACCESS, IMPORT, INSTALL, LOCK_ACCOUNT, MANAGE, MOVE, OPEN, RE_INSTALL, RE_START, READ, REFRESH, REMOVE_ACCESS, RENAME, RENEW, REJECT_ACCESS_REQUEST, REQUEST_TO_RESET_PASSWORD, RESET_PASSWORD, RESTORE_ACCESS_RIGHT, RESUME, RISE_EXCEPTION, RUN, SIGN_IN, SIGN_OUT, SIGN_UP, START, STOP, SUSPEND, SWITCH_CUSTOMER, SWITCH_OWNER, UPDATE, UPLOAD
}
