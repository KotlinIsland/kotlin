/*
 * Copyright 2010-2018 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.fir.resolve.providers.impl

import org.jetbrains.kotlin.fir.resolve.providers.FirSymbolProvider
import org.jetbrains.kotlin.fir.resolve.providers.FirSymbolProviderInternals
import org.jetbrains.kotlin.fir.scopes.FirScope
import org.jetbrains.kotlin.fir.symbols.impl.FirCallableSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirClassLikeSymbol
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.utils.addToStdlib.firstNotNullResult

class FirCompositeSymbolProvider(val providers: List<FirSymbolProvider>) : FirSymbolProvider() {
    override fun getTopLevelCallableSymbols(packageFqName: FqName, name: Name): List<FirCallableSymbol<*>> {
        return providers.flatMap { it.getTopLevelCallableSymbols(packageFqName, name) }
    }

    @FirSymbolProviderInternals
    override fun getTopLevelCallableSymbolsTo(destination: MutableList<FirCallableSymbol<*>>, packageFqName: FqName, name: Name) {
        destination += getTopLevelCallableSymbols(packageFqName, name)
    }

    override fun getNestedClassifierScope(classId: ClassId): FirScope? {
        return providers.firstNotNullResult { it.getNestedClassifierScope(classId) }
    }

    override fun getPackage(fqName: FqName): FqName? {
        return providers.firstNotNullResult { it.getPackage(fqName) }
    }

    override fun getClassLikeSymbolByFqName(classId: ClassId): FirClassLikeSymbol<*>? {
        return providers.firstNotNullResult { it.getClassLikeSymbolByFqName(classId) }
    }

    override fun getClassNamesInPackage(fqName: FqName): Set<Name> {
        return providers.flatMapTo(mutableSetOf()) { it.getClassNamesInPackage(fqName) }
    }
}
