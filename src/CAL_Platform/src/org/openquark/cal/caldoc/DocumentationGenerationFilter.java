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
 * DocumentationGenerationFilter.java
 * Creation date: Oct 14, 2005.
 * By: Joseph Wong
 */
package org.openquark.cal.caldoc;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.Scope;
import org.openquark.cal.compiler.ScopedEntity;
import org.openquark.cal.filter.ModuleFilter;
import org.openquark.cal.filter.ScopedEntityFilter;


/**
 * A DocumentationGenerationFilter is a combination of a ModuleFilter and a ScopedEntityFilter,
 * used for determining whether a module or a scoped entity should have its documentation generated.
 * 
 * In particular, documentation is generated for a scoped entity if and only if the
 * scoped entity filter accepts it <em>and</em> the module filter accepts the entity's module.
 *
 * @author Joseph Wong
 */
final class DocumentationGenerationFilter {

    /** The constituent module filter. */
    private final ModuleFilter moduleFilter;
    
    /** The constituent scoped entity filter. */
    private final ScopedEntityFilter scopedEntityFilter;
    
    /**
     * Constructs a DocumentationGenerationFilter.
     * @param moduleFilter the module filter to use.
     * @param scopedEntityFilter the scoped entity to use.
     */
    DocumentationGenerationFilter(ModuleFilter moduleFilter, ScopedEntityFilter scopedEntityFilter) {
        AbstractDocumentationGenerator.verifyArg(moduleFilter, "moduleFilter");
        this.moduleFilter = moduleFilter;
        
        AbstractDocumentationGenerator.verifyArg(scopedEntityFilter, "scopedEntityFilter");
        this.scopedEntityFilter = scopedEntityFilter;
    }

    /**
     * Returns whether the named module should have its documentation generated.
     * @param moduleName the module name.
     * @return true if the named module should have its documentation generated; false otherwise.
     */
    boolean shouldGenerateModule(ModuleName moduleName) {
        return moduleFilter.acceptModule(moduleName);
    }
    
    /**
     * Determines whether documentation should be generated for the given entity,
     * taking into account that documentation should not be generated if documentation for its module
     * is not to be generated.
     * 
     * @param entity the scoped entity.
     * @return true if documentation for the entity's module and the entity itself is to be generated; false otherwise.
     */
    boolean shouldGenerateScopedEntity(ScopedEntity entity) {
        return shouldGenerateModule(entity.getName().getModuleName()) && scopedEntityFilter.acceptScopedEntity(entity);
    }
    
    /**
     * Determines whether the given entity should be accepted, only taking into account the scope of the entity.
     * 
     * @param entity the scoped entity.
     * @return true if the entity is accepted; false otherwise.
     */
    boolean shouldAcceptScopedEntityBasedOnScopeOnly(ScopedEntity entity) {
        return scopedEntityFilter.acceptBasedOnScopeOnly(entity.getScope());
    }
    
    /**
     * Determines whether the entities of the given scope should be accepted, only taking into account the scope.
     * 
     * @param scope the scope.
     * @return true if the entities of the given scope are accepted; false otherwise.
     */
    boolean shouldAcceptBasedOnScopeOnly(Scope scope) {
        return scopedEntityFilter.acceptBasedOnScopeOnly(scope);
    }
}
