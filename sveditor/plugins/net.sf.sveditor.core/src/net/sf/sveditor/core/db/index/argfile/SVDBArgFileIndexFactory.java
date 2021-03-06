/****************************************************************************
 * Copyright (c) 2008-2010 Matthew Ballance and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthew Ballance - initial implementation
 ****************************************************************************/


package net.sf.sveditor.core.db.index.argfile;

import net.sf.sveditor.core.db.index.ISVDBFileSystemProvider;
import net.sf.sveditor.core.db.index.ISVDBIndex;
import net.sf.sveditor.core.db.index.ISVDBIndexFactory;
import net.sf.sveditor.core.db.index.SVDBIndexConfig;
import net.sf.sveditor.core.db.index.SVDBWSFileSystemProvider;
import net.sf.sveditor.core.db.index.cache.ISVDBIndexCache;
import net.sf.sveditor.core.db.index.old.SVDBArgFileIndex;

public class SVDBArgFileIndexFactory implements ISVDBIndexFactory {
	
	public static final String	TYPE = "net.sf.sveditor.argFileIndex";
	
	public static final boolean		fUseArgFile2Index = true;

	public ISVDBIndex createSVDBIndex(
			String 					projectName, 
			String 					base_location,
			ISVDBIndexCache			cache,
			SVDBIndexConfig 		config) {
		ISVDBFileSystemProvider fs_provider;
		
		fs_provider = new SVDBWSFileSystemProvider();
		
		ISVDBIndex ret = null;

		if (fUseArgFile2Index) {
			ret = new SVDBArgFileIndex2(
				projectName, base_location, fs_provider, cache, config);
		} else {
			ret = new SVDBArgFileIndex(
				projectName, base_location, fs_provider, cache, config);
		}
		
		return ret;
	}

}

