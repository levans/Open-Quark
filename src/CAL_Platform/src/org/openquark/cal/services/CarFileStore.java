/*
 * Copyright (c) 2007 BUSINESS OBJECTS SOFTWARE LIMITED
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 *     * Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 * 
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 * 
 *     * Neither the name of Business Objects nor the names of its contributors
 *       may be used to endorse or promote products derived from this software
 *       without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */


/*
 * CarFileStore.java
 * Creation date: Jan 13, 2006.
 * By: Joseph Wong
 */
package org.openquark.cal.services;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * A CAL Archive (Car) store using files and folders in the file system.
 *
 * @author Joseph Wong
 */
class CarFileStore extends ResourceFileStore implements CarStore {
    
    /** The factory for making Cars. */
    private final Car.Factory carFactory;

    /**
     * Constructs a CarFileStore.
     * @param rootDirectory the directory under which the resource folder resides.
     */
    public CarFileStore(File rootDirectory) {
        super(rootDirectory, WorkspaceResource.CAR_RESOURCE_TYPE, CarPathMapper.INSTANCE);
        this.carFactory = new Car.Factory();
    }

    /**
     * {@inheritDoc}
     */
    public Set<ResourceName> getCarNames() {
        ResourcePath.Folder baseResourceFolder = getPathMapper().getBaseResourceFolder();
        File baseResourceDirectory = FileSystemResourceHelper.getResourceFile(baseResourceFolder, getRootDirectory(), false);
        return new HashSet<ResourceName>(FileSystemResourceHelper.getDirectoryResourceNames(baseResourceDirectory, null, baseResourceFolder, getPathMapper()));
    }

    /**
     * {@inheritDoc}
     */
    public Car getCar(String carName) {
        ResourcePath.Folder baseResourceFolder = getPathMapper().getBaseResourceFolder();
        File baseResourceDirectory = FileSystemResourceHelper.getResourceFile(baseResourceFolder, getRootDirectory(), false);
        
        File carFile = new File(baseResourceDirectory, carName);
        return carFactory.getCar(carName, carFile);
    }

    /**
     * {@inheritDoc}
     */
    public Iterator<WorkspaceResource> getResourceIterator() {
        return CarPathStoreHelper.getResourceIterator(this);
    }
}
