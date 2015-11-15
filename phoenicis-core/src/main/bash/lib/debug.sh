#!/usr/bin/env bash

# Copyright (C) 2007-2011 PlayOnLinux Team
# Copyright (C) 2007-2011 Pâris Quentin
# Copyright (C) 2015 Pâris Quentin

# This program is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation; either version 2 of the License, or
# (at your option) any later version.

# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
# GNU General Public License for more details.

# You should have received a copy of the GNU General Public License along
# with this program; if not, write to the Free Software Foundation, Inc.,
# 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.

# IMPORTANT
# ---------
# Please note that this library is deprecated and only intended for PlayOnLinux v4 backward compatibility

POL_Debug_Init()
{
    POL_Debug_Message "Note: POL_Debug_Init has been called"
}

POL_Debug_Fatal ()
{
    # FIXME: Close the setupWindow properly before throwing an exception
    local message="$1"
    POL_Debug_Error "($(Get_CurrentDate)) $message"
    POL_SetupWindow_Close
    throw "$message"
}

POL_Debug_Error ()
{
    # FIXME: Show a popup message
    javaecho "($(Get_CurrentDate)) [ERROR] $@"
}

POL_Debug_LogToPrefix()
{
	javaecho "[$(Get_CurrentDate)] - $@" >> "$WINEPREFIX/playonlinux.log"
}

POL_Debug_Message ()
{
    javaecho "($(Get_CurrentDate)) [INFO] $@"
}

POL_Debug_Warning ()
{
    javaecho "($(Get_CurrentDate)) [WARNING] $@"
}

throw () {
    local message="$1"
    toPython "POL_Throw" "$message"
    exit 1
}

javaecho ()
{
    local message="$1"
    toPython "POL_Print" "$message"
}


Get_CurrentDate()
{
    # Return the current date
    date "+%D %T"
}
