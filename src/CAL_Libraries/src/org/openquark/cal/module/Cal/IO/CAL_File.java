/*
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (CAL_File.java)
 * was generated from CAL module: Cal.IO.File.
 * The constants and methods provided are intended to facilitate accessing the
 * Cal.IO.File module from Java code.
 *  
 * Creation date: Tue Oct 23 09:50:00 PDT 2007
 * --!>
 *  
 */

package org.openquark.cal.module.Cal.IO;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.SourceModel;

/**
 * The File module defines types and functions for working with files and
 * directories.
 * <p>
 * The functions in this module work on all platforms. However, the semantics of
 * some of these functions are platform-dependent, e.g. the treatment of paths
 * and path separators.
 * <p>
 * If an error occurs during the course of an IO operation, an <code>Cal.IO.File.IOError</code>
 * value is constructed and returned to the caller. While each <code>Cal.IO.File.IOError</code>
 * value contains an <code>Cal.IO.File.IOErrorType</code> value, representing the type of the error, the
 * translation of the underlying Java IOException to an <code>Cal.IO.File.IOErrorType</code> value can be
 * regarded as a best-effort attempt. In particular, the accuracy of the <code>Cal.IO.File.IOErrorType</code>
 * value for an <code>Cal.IO.File.IOError</code> is platform-dependent. In the worst case,
 * the <code>Cal.IO.File.IOError</code> would have an error type that represents an unknown
 * IOException. However, the caller can count on the one-to-one correspondence between a Java
 * IOException and a CAL <code>Cal.IO.File.IOError</code> value on all platforms. In other words,
 * no error goes unreported.
 * 
 * @author Joseph Wong
 */
public final class CAL_File {
	public static final ModuleName MODULE_NAME = ModuleName.make("Cal.IO.File");

	/**
	 * This inner class (TypeConstructors) contains constants
	 * and methods related to binding to CAL TypeConstructors in the Cal.IO.File module.
	 */
	public static final class TypeConstructors {
		/**
		 * A representation of file and directory paths.
		 * <p>
		 * The conversion of a path string to or from a <code>FileName</code> is platform-dependent,
		 * due to the platform-dependent nature of the path separator character.
		 * <p>
		 * A path may be either absolute or relative. An absolute path completely specifies
		 * the location of a file or directory. Relative paths, on the other hand, must first be
		 * resolved against the current user directory.
		 */
		public static final QualifiedName FileName = 
			QualifiedName.make(CAL_File.MODULE_NAME, "FileName");

		/**
		 * This is the CAL representation of an IO error. In particular, a Java
		 * IOException is translated into an <code>IOError</code> value.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.IO.File.ioTry
		 * </dl>
		 */
		public static final QualifiedName IOError = 
			QualifiedName.make(CAL_File.MODULE_NAME, "IOError");

		/**
		 * This is an unordered enumeration of the recognized types of IO errors. The
		 * error type of an IO error arising from a call to a Java method is determined
		 * by the corresponding Java IOException.
		 * 
		 * <dl><dt><b>See Also:</b>
		 * <dd><b>Functions and Class Methods:</b> Cal.IO.File.jIOExceptionToIOErrorType
		 * </dl>
		 */
		public static final QualifiedName IOErrorType = 
			QualifiedName.make(CAL_File.MODULE_NAME, "IOErrorType");

	}
	/**
	 * This inner class (Functions) contains constants
	 * and methods related to binding to CAL functions in the Cal.IO.File module.
	 */
	public static final class Functions {
		/**
		 * Appends the specified contents to the file specified by the file name.
		 * @param fileName (CAL type: <code>Cal.IO.File.FileName</code>)
		 *          the path of the file to be appended.
		 * @param contents (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the contents to be appended.
		 * @return (CAL type: <code>Cal.Core.Prelude.Either Cal.IO.File.IOError ()</code>) 
		 *          either a <code>Cal.Core.Prelude.Left ioError</code> indicating that the operation has failed, or a <code>Cal.Core.Prelude.Right ()</code> indicating success.
		 */
		public static final SourceModel.Expr appendFile(SourceModel.Expr fileName, SourceModel.Expr contents) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.appendFile), fileName, contents});
		}

		/**
		 * @see #appendFile(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param fileName
		 * @param contents
		 * @return the SourceModel.Expr representing an application of appendFile
		 */
		public static final SourceModel.Expr appendFile(SourceModel.Expr fileName, java.lang.String contents) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.appendFile), fileName, SourceModel.Expr.makeStringValue(contents)});
		}

		/**
		 * Name binding for function: appendFile.
		 * @see #appendFile(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName appendFile = 
			QualifiedName.make(CAL_File.MODULE_NAME, "appendFile");

		/**
		 * Appends the specified binary contents to the file specified by the file name.
		 * @param fileName (CAL type: <code>Cal.IO.File.FileName</code>)
		 *          the path of the file to be appended.
		 * @param contents (CAL type: <code>Cal.Collections.Array.Array Cal.Core.Prelude.Byte</code>)
		 *          the contents to be appended.
		 * @return (CAL type: <code>Cal.Core.Prelude.Either Cal.IO.File.IOError ()</code>) 
		 *          either a <code>Cal.Core.Prelude.Left ioError</code> indicating that the operation has failed, or a <code>Cal.Core.Prelude.Right ()</code> indicating success.
		 */
		public static final SourceModel.Expr appendFileBinary(SourceModel.Expr fileName, SourceModel.Expr contents) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.appendFileBinary), fileName, contents});
		}

		/**
		 * Name binding for function: appendFileBinary.
		 * @see #appendFileBinary(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName appendFileBinary = 
			QualifiedName.make(CAL_File.MODULE_NAME, "appendFileBinary");

		/**
		 * Constructs a file path from a list of components. For example, a Windows path
		 * 'C:\windows\explorer.exe' would have as components the list
		 * <code>["C:", "windows", "explorer.exe"]</code>. This function is meant to facilitate
		 * the construction of file paths in a platform-independent (or more specifically,
		 * path-separator-independent) manner.
		 * @param components (CAL type: <code>[Cal.Core.Prelude.String]</code>)
		 *          the components of the new path.
		 * @return (CAL type: <code>Cal.IO.File.FileName</code>) 
		 *          the file path constructed from the list of components.
		 */
		public static final SourceModel.Expr buildPathFromComponents(SourceModel.Expr components) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.buildPathFromComponents), components});
		}

		/**
		 * Name binding for function: buildPathFromComponents.
		 * @see #buildPathFromComponents(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName buildPathFromComponents = 
			QualifiedName.make(CAL_File.MODULE_NAME, "buildPathFromComponents");

		/**
		 * Creates the specified directory. The directory's parent must already exist.
		 * @param dirName (CAL type: <code>Cal.IO.File.FileName</code>)
		 *          the path of the directory.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if the operation succeeds; <code>Cal.Core.Prelude.False</code> otherwise
		 */
		public static final SourceModel.Expr createDirectory(SourceModel.Expr dirName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.createDirectory), dirName});
		}

		/**
		 * Name binding for function: createDirectory.
		 * @see #createDirectory(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName createDirectory = 
			QualifiedName.make(CAL_File.MODULE_NAME, "createDirectory");

		/**
		 * Creates the specified directory, and all nonexistent parent directories.
		 * @param dirName (CAL type: <code>Cal.IO.File.FileName</code>)
		 *          the path of the directory.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if the operation succeeds; <code>Cal.Core.Prelude.False</code> otherwise
		 */
		public static final SourceModel.Expr createDirectoryAndNonexistentParentDirectories(SourceModel.Expr dirName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.createDirectoryAndNonexistentParentDirectories), dirName});
		}

		/**
		 * Name binding for function: createDirectoryAndNonexistentParentDirectories.
		 * @see #createDirectoryAndNonexistentParentDirectories(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName createDirectoryAndNonexistentParentDirectories = 
			QualifiedName.make(
				CAL_File.MODULE_NAME, 
				"createDirectoryAndNonexistentParentDirectories");

		/**
		 * Deletes the specified directory. The directory must be empty; otherwise it
		 * will not be deleted.
		 * @param dirName (CAL type: <code>Cal.IO.File.FileName</code>)
		 *          the path of the directory.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if the operation succeeds; <code>Cal.Core.Prelude.False</code> otherwise
		 */
		public static final SourceModel.Expr deleteDirectory(SourceModel.Expr dirName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.deleteDirectory), dirName});
		}

		/**
		 * Name binding for function: deleteDirectory.
		 * @see #deleteDirectory(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName deleteDirectory = 
			QualifiedName.make(CAL_File.MODULE_NAME, "deleteDirectory");

		/**
		 * Recursively deletes the directory tree rooted at the specified directory.
		 * @param dirName (CAL type: <code>Cal.IO.File.FileName</code>)
		 *          the path of the directory.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          true if the operation succeeds; false otherwise
		 */
		public static final SourceModel.Expr deleteDirectoryTree(SourceModel.Expr dirName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.deleteDirectoryTree), dirName});
		}

		/**
		 * Name binding for function: deleteDirectoryTree.
		 * @see #deleteDirectoryTree(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName deleteDirectoryTree = 
			QualifiedName.make(CAL_File.MODULE_NAME, "deleteDirectoryTree");

		/**
		 * Deletes the specified file.
		 * @param fileName (CAL type: <code>Cal.IO.File.FileName</code>)
		 *          the path of the file.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if the operation succeeds; <code>Cal.Core.Prelude.False</code> otherwise
		 */
		public static final SourceModel.Expr deleteFile(SourceModel.Expr fileName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.deleteFile), fileName});
		}

		/**
		 * Name binding for function: deleteFile.
		 * @see #deleteFile(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName deleteFile = 
			QualifiedName.make(CAL_File.MODULE_NAME, "deleteFile");

		/**
		 * Deletes the specified file or directory. If it is a directory, it must be
		 * empty; otherwise it will not be deleted.
		 * @param fileOrDirName (CAL type: <code>Cal.IO.File.FileName</code>)
		 *          the file or path of the directory.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if the operation succeeds; <code>Cal.Core.Prelude.False</code> otherwise
		 */
		public static final SourceModel.Expr deleteFileOrDirectory(SourceModel.Expr fileOrDirName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.deleteFileOrDirectory), fileOrDirName});
		}

		/**
		 * Name binding for function: deleteFileOrDirectory.
		 * @see #deleteFileOrDirectory(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName deleteFileOrDirectory = 
			QualifiedName.make(CAL_File.MODULE_NAME, "deleteFileOrDirectory");

		/**
		 * Returns whether the file or directory denoted by the given path exists.
		 * @param fileOrDirName (CAL type: <code>Cal.IO.File.FileName</code>)
		 *          the path to check.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if the specified path refers to an existent file or directory; <code>Cal.Core.Prelude.False</code> otherwise.
		 */
		public static final SourceModel.Expr doesFileOrDirectoryExist(SourceModel.Expr fileOrDirName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.doesFileOrDirectoryExist), fileOrDirName});
		}

		/**
		 * Name binding for function: doesFileOrDirectoryExist.
		 * @see #doesFileOrDirectoryExist(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName doesFileOrDirectoryExist = 
			QualifiedName.make(CAL_File.MODULE_NAME, "doesFileOrDirectoryExist");

		/**
		 * Constructs a new file path representing a file or a subdirectory in a given
		 * parent directory.
		 * @param parent (CAL type: <code>Cal.IO.File.FileName</code>)
		 *          the parent path of the new path.
		 * @param child (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the last component of the new path.
		 * @return (CAL type: <code>Cal.IO.File.FileName</code>) 
		 *          a new file path representing a file or a subdirectory in the given
		 * parent directory.
		 */
		public static final SourceModel.Expr extendPath(SourceModel.Expr parent, SourceModel.Expr child) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extendPath), parent, child});
		}

		/**
		 * @see #extendPath(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param parent
		 * @param child
		 * @return the SourceModel.Expr representing an application of extendPath
		 */
		public static final SourceModel.Expr extendPath(SourceModel.Expr parent, java.lang.String child) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.extendPath), parent, SourceModel.Expr.makeStringValue(child)});
		}

		/**
		 * Name binding for function: extendPath.
		 * @see #extendPath(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName extendPath = 
			QualifiedName.make(CAL_File.MODULE_NAME, "extendPath");

		/**
		 * Extracts the return value from an
		 * <code>(Cal.Core.Prelude.Either Cal.IO.File.IOError a)</code> value. If the supplied
		 * value in fact represents an <code>Cal.IO.File.IOError</code>, then the return value of a call to
		 * <code>Cal.Core.Prelude.error</code> is returned. This function is therefore analogous to <code>Cal.Core.Prelude.fromJust</code>.
		 * @param resultOrError (CAL type: <code>Cal.Core.Prelude.Either Cal.IO.File.IOError a</code>)
		 *          an <code>(Either IOError a)</code> value to be processed.
		 * @return (CAL type: <code>a</code>) 
		 *          either the intended return value of a successful IO operation, or a call to <code>Cal.Core.Prelude.error</code>
		 * if the IO operation failed.
		 */
		public static final SourceModel.Expr fromIOSuccess(SourceModel.Expr resultOrError) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fromIOSuccess), resultOrError});
		}

		/**
		 * Name binding for function: fromIOSuccess.
		 * @see #fromIOSuccess(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName fromIOSuccess = 
			QualifiedName.make(CAL_File.MODULE_NAME, "fromIOSuccess");

		/**
		 * Gets the absolute path of the specified file path.
		 * <ul>
		 *  <li>
		 *   If the path is empty, then the path of the current user directory is returned.
		 *   
		 *  </li>
		 *  <li>
		 *   On UNIX, the relative path is resolved against the current user directory.
		 *   
		 *  </li>
		 *  <li>
		 *   On Windows, the relative path is resolved against the current directory of
		 *   the drive named by the path. If the path does not name a specific drive, then it is
		 *   resolved against the current user directory.
		 *   
		 *  </li>
		 * </ul>
		 * 
		 * @param fileName (CAL type: <code>Cal.IO.File.FileName</code>)
		 *          the path whose absolute path is to be returned.
		 * @return (CAL type: <code>Cal.IO.File.FileName</code>) 
		 *          the absolute path of the specified file path.
		 */
		public static final SourceModel.Expr getAbsolutePath(SourceModel.Expr fileName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getAbsolutePath), fileName});
		}

		/**
		 * Name binding for function: getAbsolutePath.
		 * @see #getAbsolutePath(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getAbsolutePath = 
			QualifiedName.make(CAL_File.MODULE_NAME, "getAbsolutePath");

		/**
		 * Gets the canonical path of the specified file path.
		 * <p>
		 * A canonical path is an absolute path without redundant path components such as "." and "..",
		 * and satisfying platform-dependent rules: (e.g. converting 'c:\' to 'C:\' on Windows, or resolving
		 * symlinks on UNIX).
		 * 
		 * @param fileName (CAL type: <code>Cal.IO.File.FileName</code>)
		 *          the path whose canonical path is to be returned.
		 * @return (CAL type: <code>Cal.IO.File.FileName</code>) 
		 *          the canonical path of the specified file path.
		 */
		public static final SourceModel.Expr getCanonicalPath(SourceModel.Expr fileName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getCanonicalPath), fileName});
		}

		/**
		 * Name binding for function: getCanonicalPath.
		 * @see #getCanonicalPath(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getCanonicalPath = 
			QualifiedName.make(CAL_File.MODULE_NAME, "getCanonicalPath");

		/**
		 * Gets the path of the current working directory.
		 * @return (CAL type: <code>Cal.IO.File.FileName</code>) 
		 *          the path of the current working directory.
		 */
		public static final SourceModel.Expr getCurrentDirectory() {
			return SourceModel.Expr.Var.make(Functions.getCurrentDirectory);
		}

		/**
		 * Name binding for function: getCurrentDirectory.
		 * @see #getCurrentDirectory()
		 */
		public static final QualifiedName getCurrentDirectory = 
			QualifiedName.make(CAL_File.MODULE_NAME, "getCurrentDirectory");

		/**
		 * Gets a list of all the files and subdirectories within the specified
		 * directory.
		 * <p>
		 * If the specified path does not denote a directory, then this function returns
		 * an empty list. Otherwise a list of <code>Cal.IO.File.FileName</code>s is returned,
		 * one for each file or directory in the directory.
		 * <p>
		 * If the specified path is absolute the returned paths are all absolute, and similarly if
		 * the specified path is relative then the returned paths are all relative.
		 * <p>
		 * The returned list is an <em>unordered</em> list of paths.
		 * 
		 * @param dirName (CAL type: <code>Cal.IO.File.FileName</code>)
		 *          the path of the directory.
		 * @return (CAL type: <code>[Cal.IO.File.FileName]</code>) 
		 *          a list of the files and subdirectories within the specified directory.
		 */
		public static final SourceModel.Expr getDirectoryContents(SourceModel.Expr dirName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getDirectoryContents), dirName});
		}

		/**
		 * Name binding for function: getDirectoryContents.
		 * @see #getDirectoryContents(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getDirectoryContents = 
			QualifiedName.make(CAL_File.MODULE_NAME, "getDirectoryContents");

		/**
		 * Returns the length, in bytes, of the file.
		 * <p>
		 * The return value is 0 if the file does not exist, and is unspecified if the
		 * path denotes a directory.
		 * 
		 * @param fileName (CAL type: <code>Cal.IO.File.FileName</code>)
		 *          the file whose length is to be returned.
		 * @return (CAL type: <code>Cal.Core.Prelude.Long</code>) 
		 *          the length, in bytes, of the file.
		 */
		public static final SourceModel.Expr getFileLength(SourceModel.Expr fileName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getFileLength), fileName});
		}

		/**
		 * Name binding for function: getFileLength.
		 * @see #getFileLength(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getFileLength = 
			QualifiedName.make(CAL_File.MODULE_NAME, "getFileLength");

		/**
		 * Fetches a list of files, or a list of files and subdirectories, in a directory
		 * matching the specified pattern (such as <code>"*.txt"</code> or <code>"test?.rpt"</code>).
		 * <p>
		 * If the specified path does not denote a directory, then this function returns
		 * an empty list. Otherwise a list of <code>Cal.IO.File.FileName</code>s is returned, one for each file
		 * in the directory, and if the <code>filesOnly</code> argument is false, one for each
		 * subdirectory in the directory as well.
		 * <p>
		 * If the specified path is absolute the returned paths are all absolute, and similarly if
		 * the specified path is relative then the returned paths are all relative.
		 * <p>
		 * The returned list is an <em>unordered</em> list of paths.
		 * <p>
		 * The file name pattern admits the use of the wildcard characters <code>'*'</code> and <code>'?'</code>. The
		 * wildcard <code>'*'</code> stands for 0 or more characters, and the wildcard <code>'?'</code> stands for a
		 * single character.
		 * 
		 * @param dirName (CAL type: <code>Cal.IO.File.FileName</code>)
		 *          the path of the directory.
		 * @param fileNamePattern (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the file name pattern to use.
		 * @param filesOnly (CAL type: <code>Cal.Core.Prelude.Boolean</code>)
		 *          whether the list is to contain files only.
		 * @return (CAL type: <code>[Cal.IO.File.FileName]</code>) 
		 *          a list of the files and subdirectories within the specified directory.
		 */
		public static final SourceModel.Expr getFilteredDirectoryContents(SourceModel.Expr dirName, SourceModel.Expr fileNamePattern, SourceModel.Expr filesOnly) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getFilteredDirectoryContents), dirName, fileNamePattern, filesOnly});
		}

		/**
		 * @see #getFilteredDirectoryContents(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param dirName
		 * @param fileNamePattern
		 * @param filesOnly
		 * @return the SourceModel.Expr representing an application of getFilteredDirectoryContents
		 */
		public static final SourceModel.Expr getFilteredDirectoryContents(SourceModel.Expr dirName, java.lang.String fileNamePattern, boolean filesOnly) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getFilteredDirectoryContents), dirName, SourceModel.Expr.makeStringValue(fileNamePattern), SourceModel.Expr.makeBooleanValue(filesOnly)});
		}

		/**
		 * Name binding for function: getFilteredDirectoryContents.
		 * @see #getFilteredDirectoryContents(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getFilteredDirectoryContents = 
			QualifiedName.make(
				CAL_File.MODULE_NAME, 
				"getFilteredDirectoryContents");

		/**
		 * Gets the last modification time of the specified file or directory.
		 * @param fileName (CAL type: <code>Cal.IO.File.FileName</code>)
		 *          the file whose last modification time is to be returned.
		 * @return (CAL type: <code>Cal.Utilities.Time.Time</code>) 
		 *          the last modification time of the specified file or directory.
		 */
		public static final SourceModel.Expr getModificationTime(SourceModel.Expr fileName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getModificationTime), fileName});
		}

		/**
		 * Name binding for function: getModificationTime.
		 * @see #getModificationTime(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getModificationTime = 
			QualifiedName.make(CAL_File.MODULE_NAME, "getModificationTime");

		/**
		 * Gets the file name from the file path. This is simply the last component of
		 * the path. For example, a Windows path 'C:\windows\explorer.exe' would yield
		 * "explorer.exe" as the file name.
		 * @param fileName (CAL type: <code>Cal.IO.File.FileName</code>)
		 *          the path from which the file name is to be extracted.
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          the file name portion of the file path.
		 */
		public static final SourceModel.Expr getNameFromPath(SourceModel.Expr fileName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getNameFromPath), fileName});
		}

		/**
		 * Name binding for function: getNameFromPath.
		 * @see #getNameFromPath(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getNameFromPath = 
			QualifiedName.make(CAL_File.MODULE_NAME, "getNameFromPath");

		/**
		 * Gets the file path's parent. For example, a Windows path
		 * 'C:\windows\explorer.exe' would yield 'C:\windows' as the parent.
		 * @param fileName (CAL type: <code>Cal.IO.File.FileName</code>)
		 *          the path whose parent path is to be returned.
		 * @return (CAL type: <code>Cal.Core.Prelude.Maybe Cal.IO.File.FileName</code>) 
		 *          <code>Cal.Core.Prelude.Just parent</code>, or <code>Cal.Core.Prelude.Nothing</code> if the path has no parent.
		 */
		public static final SourceModel.Expr getParentFromPath(SourceModel.Expr fileName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getParentFromPath), fileName});
		}

		/**
		 * Name binding for function: getParentFromPath.
		 * @see #getParentFromPath(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getParentFromPath = 
			QualifiedName.make(CAL_File.MODULE_NAME, "getParentFromPath");

		/**
		 * Gets the file path represented by the given <code>Cal.IO.File.FileName</code> value as a string.
		 * @param fileName (CAL type: <code>Cal.IO.File.FileName</code>)
		 *          the <code>FileName</code> value whose path string is to be extracted.
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          the file path as a string.
		 */
		public static final SourceModel.Expr getPathFromFileName(SourceModel.Expr fileName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.getPathFromFileName), fileName});
		}

		/**
		 * Name binding for function: getPathFromFileName.
		 * @see #getPathFromFileName(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName getPathFromFileName = 
			QualifiedName.make(CAL_File.MODULE_NAME, "getPathFromFileName");

		/**
		 * Gets the error location of the specified IO error.
		 * @param ioError (CAL type: <code>Cal.IO.File.IOError</code>)
		 *          the IO error.
		 * @return (CAL type: <code>Cal.Core.Prelude.String</code>) 
		 *          the error location of the specified IO error.
		 */
		public static final SourceModel.Expr ioeGetErrorLocation(SourceModel.Expr ioError) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.ioeGetErrorLocation), ioError});
		}

		/**
		 * Name binding for function: ioeGetErrorLocation.
		 * @see #ioeGetErrorLocation(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName ioeGetErrorLocation = 
			QualifiedName.make(CAL_File.MODULE_NAME, "ioeGetErrorLocation");

		/**
		 * Gets the error type of the specified IO error.
		 * @param ioError (CAL type: <code>Cal.IO.File.IOError</code>)
		 *          the IO error.
		 * @return (CAL type: <code>Cal.IO.File.IOErrorType</code>) 
		 *          the error type of the specified IO error.
		 */
		public static final SourceModel.Expr ioeGetErrorType(SourceModel.Expr ioError) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.ioeGetErrorType), ioError});
		}

		/**
		 * Name binding for function: ioeGetErrorType.
		 * @see #ioeGetErrorType(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName ioeGetErrorType = 
			QualifiedName.make(CAL_File.MODULE_NAME, "ioeGetErrorType");

		/**
		 * Gets the associated exception message (if any) of the specified IO error.
		 * @param ioError (CAL type: <code>Cal.IO.File.IOError</code>)
		 *          the IO error.
		 * @return (CAL type: <code>Cal.Core.Prelude.Maybe Cal.Core.Prelude.String</code>) 
		 *          the associated exception message (if any) of the specified IO error.
		 */
		public static final SourceModel.Expr ioeGetExceptionMessage(SourceModel.Expr ioError) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.ioeGetExceptionMessage), ioError});
		}

		/**
		 * Name binding for function: ioeGetExceptionMessage.
		 * @see #ioeGetExceptionMessage(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName ioeGetExceptionMessage = 
			QualifiedName.make(CAL_File.MODULE_NAME, "ioeGetExceptionMessage");

		/**
		 * Gets the associated file name (if any) of the specified IO error.
		 * @param ioError (CAL type: <code>Cal.IO.File.IOError</code>)
		 *          the IO error.
		 * @return (CAL type: <code>Cal.Core.Prelude.Maybe Cal.IO.File.FileName</code>) 
		 *          the associated file name (if any) of the specified IO error.
		 */
		public static final SourceModel.Expr ioeGetFileName(SourceModel.Expr ioError) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.ioeGetFileName), ioError});
		}

		/**
		 * Name binding for function: ioeGetFileName.
		 * @see #ioeGetFileName(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName ioeGetFileName = 
			QualifiedName.make(CAL_File.MODULE_NAME, "ioeGetFileName");

		/**
		 * Returns whether the given path is absolute.
		 * <ul>
		 *  <li>
		 *   On UNIX, a path is absolute if it starts with <code>'/'</code>.
		 *  </li>
		 *  <li>
		 *   On Windows, a path is absolute if its starts with <code>'\'</code> or with
		 *   a drive specifier followed by <code>'\'</code>.
		 *  </li>
		 * </ul>
		 * 
		 * @param fileName (CAL type: <code>Cal.IO.File.FileName</code>)
		 *          the path to check.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if the given path is absolute; <code>Cal.Core.Prelude.False</code> otherwise.
		 */
		public static final SourceModel.Expr isAbsolutePath(SourceModel.Expr fileName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isAbsolutePath), fileName});
		}

		/**
		 * Name binding for function: isAbsolutePath.
		 * @see #isAbsolutePath(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isAbsolutePath = 
			QualifiedName.make(CAL_File.MODULE_NAME, "isAbsolutePath");

		/**
		 * Checks to see whether the error type represents an error that arose because
		 * the specified file or directory was already in use.
		 * @param ioErrorType (CAL type: <code>Cal.IO.File.IOErrorType</code>)
		 *          the error type to check.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> iff the error type represents an error that arose because
		 * the specified file or directory was already in use.
		 */
		public static final SourceModel.Expr isAlreadyInUseErrorType(SourceModel.Expr ioErrorType) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isAlreadyInUseErrorType), ioErrorType});
		}

		/**
		 * Name binding for function: isAlreadyInUseErrorType.
		 * @see #isAlreadyInUseErrorType(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isAlreadyInUseErrorType = 
			QualifiedName.make(CAL_File.MODULE_NAME, "isAlreadyInUseErrorType");

		/**
		 * Checks to see whether the error type represents an error that arose because a
		 * hardware device was not ready.
		 * @param ioErrorType (CAL type: <code>Cal.IO.File.IOErrorType</code>)
		 *          the error type to check.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> iff the error type represents an error that arose because a
		 * hardware device was not ready.
		 */
		public static final SourceModel.Expr isDeviceNotReadyErrorType(SourceModel.Expr ioErrorType) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isDeviceNotReadyErrorType), ioErrorType});
		}

		/**
		 * Name binding for function: isDeviceNotReadyErrorType.
		 * @see #isDeviceNotReadyErrorType(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isDeviceNotReadyErrorType = 
			QualifiedName.make(
				CAL_File.MODULE_NAME, 
				"isDeviceNotReadyErrorType");

		/**
		 * Returns whether the specified path refers to an existent directory.
		 * @param dirName (CAL type: <code>Cal.IO.File.FileName</code>)
		 *          the path to check.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if the specified path refers to an existent directory; <code>Cal.Core.Prelude.False</code> otherwise.
		 */
		public static final SourceModel.Expr isDirectory(SourceModel.Expr dirName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isDirectory), dirName});
		}

		/**
		 * Name binding for function: isDirectory.
		 * @see #isDirectory(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isDirectory = 
			QualifiedName.make(CAL_File.MODULE_NAME, "isDirectory");

		/**
		 * Checks to see whether the error type represents an error that arose because
		 * the specified file or directory did not exist.
		 * @param ioErrorType (CAL type: <code>Cal.IO.File.IOErrorType</code>)
		 *          the error type to check.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> iff the error type represents an error that arose because
		 * the specified file or directory did not exist.
		 */
		public static final SourceModel.Expr isDoesNotExistErrorType(SourceModel.Expr ioErrorType) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isDoesNotExistErrorType), ioErrorType});
		}

		/**
		 * Name binding for function: isDoesNotExistErrorType.
		 * @see #isDoesNotExistErrorType(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isDoesNotExistErrorType = 
			QualifiedName.make(CAL_File.MODULE_NAME, "isDoesNotExistErrorType");

		/**
		 * Returns whether the specified path refers to an existent file.
		 * @param fileName (CAL type: <code>Cal.IO.File.FileName</code>)
		 *          the path to check.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if the specified path refers to an existent file; <code>Cal.Core.Prelude.False</code> otherwise.
		 */
		public static final SourceModel.Expr isFile(SourceModel.Expr fileName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isFile), fileName});
		}

		/**
		 * Name binding for function: isFile.
		 * @see #isFile(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isFile = 
			QualifiedName.make(CAL_File.MODULE_NAME, "isFile");

		/**
		 * Returns whether the specified file or directory is hidden. (The semantics of
		 * being hidden is platform specific: on Windows this means having the 'hidden'
		 * attribute set, on UNIX this means the path of the file begins with a <code>'.'</code>.)
		 * @param fileName (CAL type: <code>Cal.IO.File.FileName</code>)
		 *          the path to check.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if the specified path refers to a hidden file or directory; <code>Cal.Core.Prelude.False</code> otherwise.
		 */
		public static final SourceModel.Expr isFileOrDirectoryHidden(SourceModel.Expr fileName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isFileOrDirectoryHidden), fileName});
		}

		/**
		 * Name binding for function: isFileOrDirectoryHidden.
		 * @see #isFileOrDirectoryHidden(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isFileOrDirectoryHidden = 
			QualifiedName.make(CAL_File.MODULE_NAME, "isFileOrDirectoryHidden");

		/**
		 * Returns whether the specified file or directory is readable.
		 * @param fileName (CAL type: <code>Cal.IO.File.FileName</code>)
		 *          the file or directory to check.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if the specified file or directory is readable; <code>Cal.Core.Prelude.False</code> otherwise.
		 */
		public static final SourceModel.Expr isFileOrDirectoryReadable(SourceModel.Expr fileName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isFileOrDirectoryReadable), fileName});
		}

		/**
		 * Name binding for function: isFileOrDirectoryReadable.
		 * @see #isFileOrDirectoryReadable(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isFileOrDirectoryReadable = 
			QualifiedName.make(
				CAL_File.MODULE_NAME, 
				"isFileOrDirectoryReadable");

		/**
		 * Returns whether the specified file or directory is writable.
		 * @param fileName (CAL type: <code>Cal.IO.File.FileName</code>)
		 *          the file or directory to check.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if the specified file or directory is writable; <code>Cal.Core.Prelude.False</code> otherwise.
		 */
		public static final SourceModel.Expr isFileOrDirectoryWritable(SourceModel.Expr fileName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isFileOrDirectoryWritable), fileName});
		}

		/**
		 * Name binding for function: isFileOrDirectoryWritable.
		 * @see #isFileOrDirectoryWritable(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isFileOrDirectoryWritable = 
			QualifiedName.make(
				CAL_File.MODULE_NAME, 
				"isFileOrDirectoryWritable");

		/**
		 * Checks to see whether the error type represents an error that arose because
		 * the hardware device (e.g. the disk) was full.
		 * @param ioErrorType (CAL type: <code>Cal.IO.File.IOErrorType</code>)
		 *          the error type to check.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> iff the error type represents an error that arose because
		 * the hardware device (e.g. the disk) was full.
		 */
		public static final SourceModel.Expr isFullErrorType(SourceModel.Expr ioErrorType) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isFullErrorType), ioErrorType});
		}

		/**
		 * Name binding for function: isFullErrorType.
		 * @see #isFullErrorType(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isFullErrorType = 
			QualifiedName.make(CAL_File.MODULE_NAME, "isFullErrorType");

		/**
		 * Determines whether an <code>(Cal.Core.Prelude.Either Cal.IO.File.IOError a)</code>
		 * value represents an IO error, i.e. in fact the value is of the form <code>Cal.Core.Prelude.Left ioError</code>.
		 * This function is therefore analogous to <code>Cal.Core.Prelude.isJust</code>.
		 * @param resultOrError (CAL type: <code>Cal.Core.Prelude.Either Cal.IO.File.IOError a</code>)
		 *          an <code>(Either IOError a)</code> value to be processed.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if the IO operation succeeded, or <code>Cal.Core.Prelude.False</code> if the IO operation failed.
		 */
		public static final SourceModel.Expr isIOSuccess(SourceModel.Expr resultOrError) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isIOSuccess), resultOrError});
		}

		/**
		 * Name binding for function: isIOSuccess.
		 * @see #isIOSuccess(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isIOSuccess = 
			QualifiedName.make(CAL_File.MODULE_NAME, "isIOSuccess");

		/**
		 * Checks to see whether the error type represents an error that arose because
		 * the user did not having sufficient privileges to perform the operation.
		 * @param ioErrorType (CAL type: <code>Cal.IO.File.IOErrorType</code>)
		 *          the error type to check.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> iff the error type represents an error that arose because
		 * the user did not having sufficient privileges to perform the operation.
		 */
		public static final SourceModel.Expr isPermissionErrorType(SourceModel.Expr ioErrorType) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isPermissionErrorType), ioErrorType});
		}

		/**
		 * Name binding for function: isPermissionErrorType.
		 * @see #isPermissionErrorType(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isPermissionErrorType = 
			QualifiedName.make(CAL_File.MODULE_NAME, "isPermissionErrorType");

		/**
		 * Checks to see whether the error type represents an unrecognized Java
		 * IOException.
		 * @param ioErrorType (CAL type: <code>Cal.IO.File.IOErrorType</code>)
		 *          the error type to check.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> iff the error type represents an unrecognized Java IOException.
		 */
		public static final SourceModel.Expr isUnhandledIOExceptionErrorType(SourceModel.Expr ioErrorType) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isUnhandledIOExceptionErrorType), ioErrorType});
		}

		/**
		 * Name binding for function: isUnhandledIOExceptionErrorType.
		 * @see #isUnhandledIOExceptionErrorType(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isUnhandledIOExceptionErrorType = 
			QualifiedName.make(
				CAL_File.MODULE_NAME, 
				"isUnhandledIOExceptionErrorType");

		/**
		 * Checks to see whether the error type represents an error that arose because
		 * the specified network host could not be found.
		 * @param ioErrorType (CAL type: <code>Cal.IO.File.IOErrorType</code>)
		 *          the error type to check.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> iff the error type represents an error that arose because
		 * the specified network host could not be found.
		 */
		public static final SourceModel.Expr isUnknownHostErrorType(SourceModel.Expr ioErrorType) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.isUnknownHostErrorType), ioErrorType});
		}

		/**
		 * Name binding for function: isUnknownHostErrorType.
		 * @see #isUnknownHostErrorType(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName isUnknownHostErrorType = 
			QualifiedName.make(CAL_File.MODULE_NAME, "isUnknownHostErrorType");

		/**
		 * Constructs a <code>Cal.IO.File.FileName</code> from a file path.
		 * @param path (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the file path to be encapsulated by the <code>FileName</code> value.
		 * @return (CAL type: <code>Cal.IO.File.FileName</code>) 
		 *          a <code>FileName</code> value.
		 */
		public static final SourceModel.Expr makeFileName(SourceModel.Expr path) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeFileName), path});
		}

		/**
		 * @see #makeFileName(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param path
		 * @return the SourceModel.Expr representing an application of makeFileName
		 */
		public static final SourceModel.Expr makeFileName(java.lang.String path) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeFileName), SourceModel.Expr.makeStringValue(path)});
		}

		/**
		 * Name binding for function: makeFileName.
		 * @see #makeFileName(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeFileName = 
			QualifiedName.make(CAL_File.MODULE_NAME, "makeFileName");

		/**
		 * Reads the contents of the file specified by its file name or URL into a byte array.
		 * @param fileNameOrUrl (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the path or the URL of the file to be read.
		 * @return (CAL type: <code>Cal.Core.Prelude.Either Cal.IO.File.IOError (Cal.Collections.Array.Array Cal.Core.Prelude.Byte)</code>) 
		 *          either a <code>Cal.Core.Prelude.Left ioError</code> indicating that the operation has failed, or a <code>Cal.Core.Prelude.Right fileContents</code> encapsulating
		 * the contents of the file.
		 */
		public static final SourceModel.Expr readBinaryContentsFromFileOrUrl(SourceModel.Expr fileNameOrUrl) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.readBinaryContentsFromFileOrUrl), fileNameOrUrl});
		}

		/**
		 * @see #readBinaryContentsFromFileOrUrl(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param fileNameOrUrl
		 * @return the SourceModel.Expr representing an application of readBinaryContentsFromFileOrUrl
		 */
		public static final SourceModel.Expr readBinaryContentsFromFileOrUrl(java.lang.String fileNameOrUrl) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.readBinaryContentsFromFileOrUrl), SourceModel.Expr.makeStringValue(fileNameOrUrl)});
		}

		/**
		 * Name binding for function: readBinaryContentsFromFileOrUrl.
		 * @see #readBinaryContentsFromFileOrUrl(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName readBinaryContentsFromFileOrUrl = 
			QualifiedName.make(
				CAL_File.MODULE_NAME, 
				"readBinaryContentsFromFileOrUrl");

		/**
		 * Reads the contents of the specified file into a string.
		 * @param fileName (CAL type: <code>Cal.IO.File.FileName</code>)
		 *          the path of the file to be read.
		 * @return (CAL type: <code>Cal.Core.Prelude.Either Cal.IO.File.IOError Cal.Core.Prelude.String</code>) 
		 *          either a <code>Cal.Core.Prelude.Left ioError</code> indicating that the operation has failed, or a <code>Cal.Core.Prelude.Right fileContents</code> encapsulating
		 * the contents of the file.
		 */
		public static final SourceModel.Expr readFile(SourceModel.Expr fileName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.readFile), fileName});
		}

		/**
		 * Name binding for function: readFile.
		 * @see #readFile(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName readFile = 
			QualifiedName.make(CAL_File.MODULE_NAME, "readFile");

		/**
		 * Reads the contents of the specified file into a byte array.
		 * @param fileName (CAL type: <code>Cal.IO.File.FileName</code>)
		 *          the path of the file to be read.
		 * @return (CAL type: <code>Cal.Core.Prelude.Either Cal.IO.File.IOError (Cal.Collections.Array.Array Cal.Core.Prelude.Byte)</code>) 
		 *          either a <code>Cal.Core.Prelude.Left ioError</code> indicating that the operation has failed, or a <code>Cal.Core.Prelude.Right fileContents</code> encapsulating
		 * the contents of the file.
		 */
		public static final SourceModel.Expr readFileBinary(SourceModel.Expr fileName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.readFileBinary), fileName});
		}

		/**
		 * Name binding for function: readFileBinary.
		 * @see #readFileBinary(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName readFileBinary = 
			QualifiedName.make(CAL_File.MODULE_NAME, "readFileBinary");

		/**
		 * Reads the lines of the specified text file.
		 * The file will be closes when the end of the input is reached.
		 * @param fileName (CAL type: <code>Cal.IO.File.FileName</code>)
		 *          the path of the file to be read
		 * @return (CAL type: <code>Cal.Core.Prelude.Either Cal.IO.File.IOError [Cal.Core.Prelude.String]</code>) 
		 *          either a <code>Cal.Core.Prelude.Left ioError</code> indicating that the operation has failed, or a 
		 * <code>Cal.Core.Prelude.Right fileContents</code> encapsulating the lines of the text file
		 */
		public static final SourceModel.Expr readFileLines(SourceModel.Expr fileName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.readFileLines), fileName});
		}

		/**
		 * Name binding for function: readFileLines.
		 * @see #readFileLines(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName readFileLines = 
			QualifiedName.make(CAL_File.MODULE_NAME, "readFileLines");

		/**
		 * Renames the specified directory.
		 * @param sourceDirName (CAL type: <code>Cal.IO.File.FileName</code>)
		 *          the name of the source directory.
		 * @param destDirName (CAL type: <code>Cal.IO.File.FileName</code>)
		 *          the name of the destination directory.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if the operation succeeds; <code>Cal.Core.Prelude.False</code> otherwise
		 */
		public static final SourceModel.Expr renameDirectory(SourceModel.Expr sourceDirName, SourceModel.Expr destDirName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.renameDirectory), sourceDirName, destDirName});
		}

		/**
		 * Name binding for function: renameDirectory.
		 * @see #renameDirectory(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName renameDirectory = 
			QualifiedName.make(CAL_File.MODULE_NAME, "renameDirectory");

		/**
		 * Renames the specified file.
		 * @param sourceFileName (CAL type: <code>Cal.IO.File.FileName</code>)
		 *          the name of the source file.
		 * @param destFileName (CAL type: <code>Cal.IO.File.FileName</code>)
		 *          the name of the destination file.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if the operation succeeds; <code>Cal.Core.Prelude.False</code> otherwise
		 */
		public static final SourceModel.Expr renameFile(SourceModel.Expr sourceFileName, SourceModel.Expr destFileName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.renameFile), sourceFileName, destFileName});
		}

		/**
		 * Name binding for function: renameFile.
		 * @see #renameFile(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName renameFile = 
			QualifiedName.make(CAL_File.MODULE_NAME, "renameFile");

		/**
		 * Renames the specified file or directory.
		 * @param source (CAL type: <code>Cal.IO.File.FileName</code>)
		 *          the name of the source file or directory.
		 * @param dest (CAL type: <code>Cal.IO.File.FileName</code>)
		 *          the name of the destination file or directory.
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 *          <code>Cal.Core.Prelude.True</code> if the operation succeeds; <code>Cal.Core.Prelude.False</code> otherwise
		 */
		public static final SourceModel.Expr renameFileOrDirectory(SourceModel.Expr source, SourceModel.Expr dest) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.renameFileOrDirectory), source, dest});
		}

		/**
		 * Name binding for function: renameFileOrDirectory.
		 * @see #renameFileOrDirectory(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName renameFileOrDirectory = 
			QualifiedName.make(CAL_File.MODULE_NAME, "renameFileOrDirectory");

		/**
		 * Writes the specified contents into the file specified by the file name. If the
		 * file already exists, it will be overwritten with by the new contents.
		 * @param fileName (CAL type: <code>Cal.IO.File.FileName</code>)
		 *          the path of the file to be written.
		 * @param contents (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the contents to be written.
		 * @return (CAL type: <code>Cal.Core.Prelude.Either Cal.IO.File.IOError ()</code>) 
		 *          either a <code>Cal.Core.Prelude.Left ioError</code> indicating that the operation has failed, or a <code>Cal.Core.Prelude.Right ()</code> indicating success.
		 */
		public static final SourceModel.Expr writeFile(SourceModel.Expr fileName, SourceModel.Expr contents) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.writeFile), fileName, contents});
		}

		/**
		 * @see #writeFile(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param fileName
		 * @param contents
		 * @return the SourceModel.Expr representing an application of writeFile
		 */
		public static final SourceModel.Expr writeFile(SourceModel.Expr fileName, java.lang.String contents) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.writeFile), fileName, SourceModel.Expr.makeStringValue(contents)});
		}

		/**
		 * Name binding for function: writeFile.
		 * @see #writeFile(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName writeFile = 
			QualifiedName.make(CAL_File.MODULE_NAME, "writeFile");

		/**
		 * Writes the specified binary contents into the file specified by the file name.
		 * If the file already exists, it will be overwritten with by the new contents.
		 * @param fileName (CAL type: <code>Cal.IO.File.FileName</code>)
		 *          the path of the file to be written.
		 * @param contents (CAL type: <code>Cal.Collections.Array.Array Cal.Core.Prelude.Byte</code>)
		 *          the contents to be written.
		 * @return (CAL type: <code>Cal.Core.Prelude.Either Cal.IO.File.IOError ()</code>) 
		 *          either a <code>Cal.Core.Prelude.Left ioError</code> indicating that the operation has failed, or a <code>Cal.Core.Prelude.Right ()</code> indicating success.
		 */
		public static final SourceModel.Expr writeFileBinary(SourceModel.Expr fileName, SourceModel.Expr contents) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.writeFileBinary), fileName, contents});
		}

		/**
		 * Name binding for function: writeFileBinary.
		 * @see #writeFileBinary(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName writeFileBinary = 
			QualifiedName.make(CAL_File.MODULE_NAME, "writeFileBinary");

	}
	/**
	 * A hash of the concatenated JavaDoc for this class (including inner classes).
	 * This value is used when checking for changes to generated binding classes.
	 */
	public static final int javaDocHash = -2063738510;

}
