package net.sf.sveditor.core.tests.index;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.sf.sveditor.core.SVCorePlugin;
import net.sf.sveditor.core.StringInputStream;
import net.sf.sveditor.core.Tuple;
import net.sf.sveditor.core.db.SVDBFile;
import net.sf.sveditor.core.db.SVDBMarker;
import net.sf.sveditor.core.db.SVDBTask;
import net.sf.sveditor.core.db.project.SVDBProjectData;
import net.sf.sveditor.core.db.project.SVDBProjectManager;
import net.sf.sveditor.core.db.project.SVProjectFileWrapper;
import net.sf.sveditor.core.tests.SVCoreTestCaseBase;
import net.sf.sveditor.core.tests.utils.TestUtils;
import net.sf.sveditor.core.utils.OverrideTaskFuncFinder;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;

public class TestArgFileParseAPI extends SVCoreTestCaseBase {
	
	public void testMissingIncludeResolved() throws CoreException {
		SVCorePlugin.getDefault().enableDebug(false);
		

		File pdir = new File(fTmpDir, "missing_inc");
		assertTrue(pdir.mkdirs());
		
		IProject p = TestUtils.createProject("missing_inc", pdir);
		addProject(p);
	
		IFolder incdir = p.getFolder("incdir");
		incdir.create(true, true, new NullProgressMonitor());
	
		TestUtils.copy(
				"class foobar;\n" +
				"endclass\n",
				incdir.getFile("foobar.svh"));
	
		String my_class1_svh = 
				"`include \"foobar.svh\"\n" +
				"class my_class1;\n" +
				"endclass\n";
		
		TestUtils.copy(my_class1_svh,
				p.getFile("my_class1.svh"));
		
		TestUtils.copy(
				"my_class1.svh",
				p.getFile("missing_inc.f"));
		
		// Setup the project
		List<SVDBMarker> markers = new ArrayList<SVDBMarker>();
		SVDBProjectManager pmgr = SVCorePlugin.getDefault().getProjMgr();
		
		SVDBProjectData pdata = pmgr.getProjectData(p);
		
		SVProjectFileWrapper fw = pdata.getProjectFileWrapper();
		
		fw.addArgFilePath("${project_loc}/missing_inc.f");
		
		pdata.setProjectFileWrapper(fw, true);
		
		// Build the project
		pmgr.rebuildProject(new NullProgressMonitor(), p);
		
		pdata.getProjectIndexMgr().parse(new NullProgressMonitor(), 
				new StringInputStream(my_class1_svh), 
				"${workspace_loc}/missing_inc/my_class1.svh", 
				markers);
		
		assertEquals(1, markers.size());
		
		// Now, add the missing include path
		TestUtils.copy(
				"+incdir+incdir\n" +
				"my_class1.svh",
				p.getFile("missing_inc.f"));		
		
		// Re-build the project
		pmgr.rebuildProject(new NullProgressMonitor(), p);
	
		// Re-parse the file
		markers.clear();
		pdata.getProjectIndexMgr().parse(new NullProgressMonitor(), 
				new StringInputStream(my_class1_svh), 
				"${workspace_loc}/missing_inc/my_class1.svh", 
				markers);
	
		// Confirm that the missing include is found now
		assertEquals(0, markers.size());
	}

	public void testTaskOverrideMarkers() throws CoreException {
		SVCorePlugin.getDefault().enableDebug(false);

		String pname = "task_override_markers";

		File pdir = new File(fTmpDir, pname);
		assertTrue(pdir.mkdirs());
		
		IProject p = TestUtils.createProject(pname, pdir);
		addProject(p);
		
		TestUtils.copy(
				"class base;\n" +
				"	virtual function func();\n" +
				"		int x;\n" +
				"	endfunction\n" +
				"endclass\n",
				p.getFile("base.sv"));
	
		TestUtils.copy(
				"`include \"base.sv\"\n" +
				"\n" +
				"class derived extends base;\n" +
				"	function func();\n" +
				"	endfunction\n" +
				"endclass\n",
				p.getFile("derived.sv"));
		
		String source =
				"`include \"derived.sv\"\n" +
				"\n" +
				"/* some comments just\n" +
				" * to take up space\n" +
				" */\n" +
				"\n" +
				"class unreleated ;\n" +
				"	function func();\n" +
				"	endfunction\n" +
				"endclass\n";
		TestUtils.copy(
				source,
				p.getFile("source.sv"));
		
		TestUtils.copy(
				"source.sv\n",
				p.getFile("list.f"));
	
		// Setup the project
		List<SVDBMarker> markers = new ArrayList<SVDBMarker>();
		SVDBProjectManager pmgr = SVCorePlugin.getDefault().getProjMgr();
		
		SVDBProjectData pdata = pmgr.getProjectData(p);
		
		SVProjectFileWrapper fw = pdata.getProjectFileWrapper();
		
		fw.addArgFilePath("${project_loc}/list.f");
		
		pdata.setProjectFileWrapper(fw, true);
		
		// Build the project
		pmgr.rebuildProject(new NullProgressMonitor(), p);
		
		Tuple<SVDBFile, SVDBFile> ret = 
				pdata.getProjectIndexMgr().parse(new NullProgressMonitor(), 
				new StringInputStream(source), 
				"${workspace_loc}/" + pname + "/source.sv",
				markers);
		
		assertEquals(0, markers.size());
	
		OverrideTaskFuncFinder finder = new OverrideTaskFuncFinder();
		List<Tuple<SVDBTask, SVDBTask>> override_tasks = finder.find(ret.second(), pdata.getProjectIndexMgr());

		assertEquals(0, override_tasks.size());
	}

	public void testMacrosDefinedInsidePackage() throws CoreException {
		SVCorePlugin.getDefault().enableDebug(false);

		String pname = "package_macros";

		File pdir = new File(fTmpDir, pname);
		assertTrue(pdir.mkdirs());
		
		IProject p = TestUtils.createProject(pname, pdir);
		addProject(p);
		
		TestUtils.copy(
				"`define MY_MACRO(p)\n",
				p.getFile("macros.svh"));

		String cls1 = 
				"class cls1;\n" +
				"	`MY_MACRO(bar)\n" +
				"	virtual function func();\n" +
				"		int x;\n" +
				"	endfunction\n" +
				"endclass\n";
		TestUtils.copy(
				cls1,
				p.getFile("cls1.svh"));
	
		TestUtils.copy(
				"package pkg;\n" +
				"	`include \"macros.svh\"\n" +
				"	`include \"cls1.svh\"\n" +
				"endpackage\n",
				p.getFile("pkg.sv"));
		
		TestUtils.copy(
				"pkg.sv\n",
				p.getFile("list.f"));
	
		// Setup the project
		List<SVDBMarker> markers = new ArrayList<SVDBMarker>();
		SVDBProjectManager pmgr = SVCorePlugin.getDefault().getProjMgr();
		
		SVDBProjectData pdata = pmgr.getProjectData(p);
		
		SVProjectFileWrapper fw = pdata.getProjectFileWrapper();
		
		fw.addArgFilePath("${project_loc}/list.f");
		
		pdata.setProjectFileWrapper(fw, true);
		
		// Build the project
		pmgr.rebuildProject(new NullProgressMonitor(), p);
		
		Tuple<SVDBFile, SVDBFile> ret = 
				pdata.getProjectIndexMgr().parse(new NullProgressMonitor(), 
				new StringInputStream(cls1), 
				"${workspace_loc}/" + pname + "/cls1.svh",
				markers);
		
		assertNotNull(ret);
		
		assertEquals(0, markers.size());
	}
}
