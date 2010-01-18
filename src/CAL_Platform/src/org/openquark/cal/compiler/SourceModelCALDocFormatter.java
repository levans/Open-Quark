package org.openquark.cal.compiler;

import org.openquark.cal.compiler.SourceModel.CALDoc;
import org.openquark.cal.compiler.SourceModel.Name;
import org.openquark.cal.compiler.SourceModelCodeFormatter.Options;
import org.openquark.util.General;

/** 
 * this is the formatter for caldoc comments 
 * Comments are first formatted without decorations (ie \/**, *, *\/) 
 * Decorations are added as the final step.
 */
public class SourceModelCALDocFormatter extends
        SourceModelTraverser<Options, Void> {

    /**
     * System Line separator. 
     */
    private static final String EOL = System.getProperty("line.separator");

    /**
     * this is the string used to separate CAL doc paragraphs 
     */
    private static final String paragraphBreak = EOL + EOL;

    /**
     * this is a regex designed to match the EOL sequence in DOS, MAC or Unix strings
     * the formatting will convert all EOL in the input caldoc comment to the system
     * standard.
     */
    final private static String EOLRegex = "(\r\n)|\n|\r";
    
    /**
     * Internal state indicated whether or not the element is contained within 
     * a pre-formatted context
     */
    private boolean isPreformatted = false;

    /**
     * internal flag is used to record when a CR is removed from 
     * the end of a CALDoc text fragment that is output. 
     * It is used to keep track of paragraph breaks that are split across 
     * adjacent plain segments e.g. The CALDoc source model could contain the 
     * following two segments: "end of paragraph1.\n", "\nStart of next paragraph"
     */
    private boolean newLineRemoved = false;

    /**
     * this is used to record where an atomic item 
     * (one that cannot be split for word wrapping) starts.
     */
    private int atomicItemStart = 0;

    /** this is the standard indent used for items and lists */
    private final static int STANDARD_INDENT = 4;

    /** this is the standard indent used for blocks, e.g. author and arg */
    private final static int BLOCK_INDENT = 2;

    /** the string used to open a caldoc comment*/
    private final static String calDocStart = "/**";
    
    /** the string used to end a caldoc comment*/
    private final static String calDocEnd = " */";
    
    /** the string used to decorate the left hand edge of caldoc comments*/
    private final static String calDocLeft = " * ";
    
    /**
     * this is the current indent in number of spaces. This is incremented and decremented
     * when the formatter enters/leaves certain blocks.
     */
    private int indent = 0;

    /** The StringBuilder used to build up the CAL source. */
    final private StringBuilder sb;

    /**
     * Create CALDocFormatter with StringBuilder to write to.
     * @param sb the stringbuilder to write the comment to.
     */
    SourceModelCALDocFormatter(StringBuilder sb) {
        this.sb = sb;
    }
   
    /**
     * trims trailing spaces from a string buffer
     * @param sb the string buffer to trim
     */
    private void trimRight(StringBuilder sb) {

        int i = sb.length() - 1;
        while (i >= 0 && sb.charAt(i) == ' ') {
            sb.deleteCharAt(i--);
        }
    }

    /**
     * decorates a comment.
     * The comment is returned as a single line comment if it is single line
     * and fits on a single line, otherwise multi line
     * @param options the options used - provides the max line width
     */
    public void decorate(Options options) {
        trimRight(sb);
        if (sb.indexOf(EOL) == -1 && sb.length() + calDocStart.length() + calDocEnd.length() + 1< options.getMaxColumns()) {
            sb.insert(0, calDocStart + " ");
            sb.append(calDocEnd + EOL);
        } else {
            int i=0;
            sb.insert(0, calDocLeft);
            while( (i = sb.indexOf(EOL, i)) > 0) {
                i += EOL.length();
                sb.insert(i, calDocLeft);
            }
            sb.insert(0, calDocStart + EOL);
            sb.append(EOL + calDocEnd + EOL);
        }
    }
    
    /**
     * Creates a string of the specified number of spaces.
     * @param nSpaces
     * @return a String containing the specified number of spaces.
     */
    private static String space(int nSpaces) {
        StringBuilder sb = new StringBuilder(nSpaces);
        for (int i = 0; i < nSpaces; ++i) {
            sb.append(' ');
        }

        return sb.toString();
    }
        
    /**
     * Escapes the specified string for inclusion in the source representation
     * of a CALDoc comment.
     * 
     * @param string
     *            the string to be escaped.
     * @return the escaped version of the specified string.
     */
    private static String caldocEscape(String string) {
        String augmentedString = EOL + string; // augment the string so that
                                                // the @-escaping can be
                                                // uniformly applied to even the
                                                // first @ after leading spaces
        String augmentedStringWithEscapedAtSigns = augmentedString.replaceAll(
                "@", "\\\\@");
        String augmentedEscapedString = augmentedStringWithEscapedAtSigns
                .replaceAll("\\{@", "\\\\{@"); // the first arg is the regular
                                                // expression '{@', the second
                                                // arg the replacement string
                                                // '\{@'
        return augmentedEscapedString.substring(EOL.length());
    }

    /**
     * Get the current offset from the start of the line.
     * 
     * @param sb
     * @return the offset from the start of the last line.
     */
    private static int getOffset(StringBuilder sb) {
        int offset = sb.length() - sb.lastIndexOf(EOL) - EOL.length();
        if (offset < 0) {
            offset = 0;
        }
        return offset;
    }

   
    /**
     * Formats the specified string into a suitably indented block of text
     * for a CALDoc comment in source form. 
     * 
     * @param string
     *            the string to be formatted.
     * @param info
     *            the current formatting information
     * @return the formatted string.
     */
    private String formatIntoCALDocLines(String string, Options info) {

        if (isCalDocContentOnLine() && sb.charAt(sb.length() -1) == ' ' &&
               string.length() >0 &&  string.charAt(0) == ' ') {
            string = string.substring(1);
        }

        if (isPreformatted) {
            return string.replaceAll(EOLRegex, EOL +space(indent));
        } else {
            
            string = wrapCalDocLines(string, getOffset(sb), info
                    .getMaxColumns());
            return string.replaceAll("(" + EOLRegex + ")[ \t\f]*", EOL +space(indent));
        }
    }

    /**
     * Merge lines by removing each single CR and replacing with a space,
     * but preserving CRCR as this indicates paragraph break.
     * 
     * @param text
     * @return the merged text
     */
    private String mergeLines(String text) {
        StringBuilder str = new StringBuilder(text);

        int i = 0;
        while (i < str.length()) {
            if (str.charAt(i) == '\n') {
                if ((i + 1 >= str.length() || str.charAt(i + 1) != '\n')) {
                    str.replace(i, i + 1, " ");

                }
                i++;
            }
            i++;
        }
        return str.toString().replaceAll(" +", " ");
    }

    /**
     * This is used to break CAlDoc text at spaces. We keep track of CR that
     * are removed as adjacent CRs that indicate a paragraph break can
     * appear across adjacent text segments.
     * 
     * @param text
     *            the text to wrap
     * @param linePos
     *            the position on the current line
     * @param width
     *            the text width.
     * @return the text with single new lines replaced by spaces
     */
    private String wrapCalDocLines(String text, int linePos, int width) {

        String normalizedText = text.replaceAll("[\t ]+", " ") // compress
                                                                // multiple
                                                                // spaces
                .replaceAll(EOLRegex, "\n") // use canonical linefeeds
                .replaceAll(" ?\n ?", "\n") // trim lines
                .replaceAll("\n[\t ]*", "\n"); // strip spaces from start of comment

        if (normalizedText.length() == 0) {
            return "";
        }

        if (normalizedText.matches("(?s)\n *")) {
            if (newLineRemoved) {
                newLineRemoved = false;
                return paragraphBreak;
            } else {
                    newLineRemoved = true;
                    return "";
            }
        }


        String prefix = "";

        if (newLineRemoved) {
            prefix = " ";
        }

        if (normalizedText.matches("(?s)\n\n.*")) {
            prefix = paragraphBreak;
            linePos = 0;
            normalizedText = normalizedText.substring(2);
            newLineRemoved = false;
        }

        StringBuilder buffer = new StringBuilder(mergeLines(normalizedText));
        int index = 0;

        if (normalizedText.matches("(?s)\n[^\n].*")) {
            if (newLineRemoved) {
                prefix = paragraphBreak;
                linePos = 0;
            }
            normalizedText = normalizedText.substring(1);
        }

        if (normalizedText.matches("(?s).*[^\n]\\n *")) {
            newLineRemoved = true;
        } else {
            newLineRemoved = false;
        }

        while (buffer.length() - index > width - linePos) {

            // if the text contains a line feed before the line stop,
            // there's
            // no need to wrap.
            int nl = buffer.indexOf("\n", index);
            if (nl > 0 && nl <= index + width - linePos) {
                index = nl + 1;
                linePos = 0;
                continue;
            }

            // find space to break line at, looking back from line limit
            int space = buffer.lastIndexOf(" ", index + width - linePos);

            // if we failed to find a space looking back, we have no choice
            // but to
            // look for the first space beyond the line limit
            if (space < index) {
                space = buffer.indexOf(" ", index + width - linePos);
            }

            if (space < 0 || space == buffer.length() - 1) {
                break;
            }

            // trim trailing spaces from line
            while (space >= index && buffer.charAt(space) == ' ') {
                buffer.delete(space, space + 1);
                space--;
            }
            space++;
            buffer.insert(space, EOL);
            index = space + EOL.length();
            linePos = 0;
        }
        return prefix + buffer.toString().replaceAll(" +", " ");
    }
    
    /**
     * @param moduleName
     *            the source model element to be traversed
     * @param options
     *            unused argument
     * @return null
     */
    public Void visit_Name_Module(Name.Module moduleName, Options options) {

        SourceModel.verifyArg(moduleName, "moduleName");

        moduleName.getQualifier().accept(this, options);

        if (moduleName.getQualifier().getNComponents() > 0) {
            sb.append(".");
        }
        sb.append(moduleName.getUnqualifiedModuleName());

        return null;
    }

    /**
     * @param qualifier
     *            the source model element to be traversed
     * @param options
     *            unused argument
     * @return null
     */
    public Void visit_Name_Module_Qualifier(
            Name.Module.Qualifier qualifier, Options options) {

        SourceModel.verifyArg(qualifier, "qualifier");
        for (int i = 0, n = qualifier.getNComponents(); i < n; ++i) {
            if (i > 0) {
                sb.append(".");
            }
            sb.append(qualifier.getNthComponents(i));
        }
        return null;
    }

    /**
     * @param cons
     *            the source model element to be traversed
     * @param options
     *            unused argument
     * @return null
     */
    public Void visit_Name_DataCons(Name.DataCons cons, Options options) {

        SourceModel.verifyArg(cons, "cons");

        if (cons.getModuleName() != null) {
            cons.getModuleName().accept(this, options);
        }

        if (cons.getModuleName() != null) {
            sb.append('.');
        }

        sb.append(cons.getUnqualifiedName());

        return null;
    }

    /**
     * @param function
     *            the source model element to be traversed
     * @param options
     *            unused argument
     * @return null
     */
    public Void visit_Name_Function(Name.Function function, Options options) {

        SourceModel.verifyArg(function, "function");

        if (function.getModuleName() != null) {
            function.getModuleName().accept(this, options);
            sb.append('.');
        }

        sb.append(function.getUnqualifiedName());
        return null;
    }

    /**
     * @param typeClass
     *            the source model element to be traversed
     * @param options
     *            unused argument
     * @return null
     */
    public Void visit_Name_TypeClass(Name.TypeClass typeClass, Options options) {

        SourceModel.verifyArg(typeClass, "typeClass");

        if (typeClass.getModuleName() != null) {
            typeClass.getModuleName().accept(this, options);
            sb.append('.');
        }

        sb.append(typeClass.getUnqualifiedName());
        return null;
    }

    /**
     * @param cons
     *            the source model element to be traversed
     * @param options
     *            unused argument
     * @return null
     */
    public Void visit_Name_TypeCons(Name.TypeCons cons, Options options) {

        SourceModel.verifyArg(cons, "cons");

        if (cons.getModuleName() != null) {
            cons.getModuleName().accept(this, options);
            sb.append('.');
        }

        sb.append(cons.getUnqualifiedName());
        return null;
    }

    /**
     * @param cons
     *            the source model element to be traversed
     * @param options
     *            unused argument
     * @return null
     */
    public Void visit_Name_WithoutContextCons(Name.WithoutContextCons cons,
            Options options) {

        SourceModel.verifyArg(cons, "cons");

        if (cons.getModuleName() != null) {
            cons.getModuleName().accept(this, options);
            sb.append('.');
        }

        sb.append(cons.getUnqualifiedName());
        return null;
    }

    //TODO-MB
    /**
     * @param comment
     *            the source model element to be traversed
     * @param options
     *            unused argument
     * @return null
     */
    protected Void visit_CALDoc_Comment_Helper(CALDoc.Comment comment,
            Options options) {

        comment.getDescription().accept(this, options);

        for (int i = 0, n = comment.getNTaggedBlocks(); i < n; i++) {
            comment.getNthTaggedBlock(i).accept(this, options);
        }

        return null;
    }

    /**
     * @param segment
     *            the source model element to be traversed
     * @param options
     *            unused argument
     * @return null
     */
    public Void visit_CALDoc_TextSegment_Plain(
            CALDoc.TextSegment.Plain segment, Options options) {

        SourceModel.verifyArg(segment, "segment");

        String escapedAndNewlineNormalizedText = caldocEscape(General
                .toPlatformLineSeparators(segment.getText()));

        //TODO-MB
        String s = formatIntoCALDocLines(escapedAndNewlineNormalizedText, options);
        
        if (!isCalDocContentOnLine()) {
            s = s.replaceAll("^ +", "");
        } 
        
        sb.append(s);

        return null;
    }

    /**
     * @param segment
     *            the source model element to be traversed
     * @param options
     *            unused argument
     * @return null
     */
    public Void visit_CALDoc_TextSegment_Preformatted(
            CALDoc.TextSegment.Preformatted segment, Options options) {

        SourceModel.verifyArg(segment, "segment");

        SourceModelCALDocFormatter segmentFormatter = new SourceModelCALDocFormatter(sb);
        segmentFormatter.isPreformatted = true;

        final int nSegments = segment.getNSegments();
        for (int i = 0; i < nSegments; i++) {
            segment.getNthSegment(i).accept(segmentFormatter, options);
        }

        return null;
    }

    /**
     * this is used to mark the beginning of a CAlDoc item that cannot be
     * split
     */
    private void beginAtomicCalDocItem() {
        atomicItemStart = sb.length();
    }

    /**
     * this is used to mark the end of an atomic caldoc item. If the item extends
     * past the line's end it is moved to the next line.
     * 
     * @param options
     */
    private void endAtomicCalDocItem(Options options) {
        assert(atomicItemStart >= 0);
        
        if (getOffset(sb) > options.getMaxColumns() &&
                sb.indexOf(EOL, atomicItemStart) == -1) {
            
            //if we are breaking at a space, the space must be removed
            if (sb.charAt(atomicItemStart) == ' ') {
                sb.deleteCharAt(atomicItemStart);
            }
            
            sb.insert(atomicItemStart, EOL + space(indent));
        }
    }

    /**
     * Starts a new calddoc line with the appropriate left margin indent
     */
    private void emitCalDocLine() {
        sb.append(EOL).append(
                        space(indent));
    }

    /**
     * Write out an atomic piece of CAL doc text to the output buffer if the
     * text is too long to fit on the current line, a new line is started.
     * 
     * Start the text on a new line if the text is too long and there is a 
     * space at the beginning.
     * 
     * @param text
     * @param options
     */
    private void emitCalDocText(String text, Options options) {
        if (text.length() == 0) {
            return;
        }
        
        if (getOffset(sb) + text.length() > options.getMaxColumns() &&
                isCalDocContentOnLine() &&
                (sb.charAt(sb.length()-1) == ' ' || text.charAt(text.length()-1) == ' ')) {
            emitCalDocLine();
            sb.append(text);
        }
        else {
            sb.append(text);
        }
    }

    /**
     * Write out a closing tag
     * 
     * Enforces the rule that there must be a space before the atomic item.
     * @param options - formatting options
     */
    private void emitCloseTag(Options options) {
        sb.append("@}");
    }
    
    /**
     * Checks to see if there is content on the current line
     * 
     * @return true if there is some content on the last caldoc line in the
     *         output buffer
     */
    private boolean isCalDocContentOnLine() {
        int start = sb.lastIndexOf(General.SYSTEM_EOL, sb.length());
        if (start < 0) {
            start = 0;
        } else {
            start += General.SYSTEM_EOL.length();
        }

        while(start < sb.length()) {
            if (sb.charAt(start++) != ' ') {
                return true;
            }
        }
        
        return false;
    }

    /**
     * Start a new CalDoc line if there is content on the current line
     */
    private void ensureOnNewLine() {
        if (isCalDocContentOnLine()) {
            emitCalDocLine();
        }
    }
    
    /**
     * @param segment
     *            the source model element to be traversed
     * @param options
     *            unused argument
     * @return null
     */
    public Void visit_CALDoc_TextSegment_InlineTag_URL(
            CALDoc.TextSegment.InlineTag.URL segment, Options options) {

        SourceModel.verifyArg(segment, "segment");

        Options info = options;

        sb.append("{@url ");

        beginAtomicCalDocItem();
        segment.getContent().accept(this, info);
        emitCloseTag(options);
        endAtomicCalDocItem(options);

        return null;
    }

    private void visit_CALDoc_TextSegment_InlineTag_Link_WithContext_Helper(
            CALDoc.TextSegment.InlineTag.Link.WithContext link, Options info) {

        emitCalDocText("{@link ", info);
        emitCalDocText(link.getContextKeyword() + " = ", info);

        beginAtomicCalDocItem();
        link.getAbstractCrossReference().accept(this, info);
        sb.append("@}");
        endAtomicCalDocItem(info);
    }

    /**
     * @param segment
     *            the source model element to be traversed
     * @param options
     *            unused argument
     * @return null
     */
    public Void visit_CALDoc_TextSegment_InlineTag_Link_Function(
            CALDoc.TextSegment.InlineTag.Link.Function segment, Options options) {

        SourceModel.verifyArg(segment, "segment");
        visit_CALDoc_TextSegment_InlineTag_Link_WithContext_Helper(segment,
                options);
        return null;
    }

    /**
     * @param segment
     *            the source model element to be traversed
     * @param options
     *            unused argument
     * @return null
     */
    public Void visit_CALDoc_TextSegment_InlineTag_Link_Module(
            CALDoc.TextSegment.InlineTag.Link.Module segment, Options options) {

        SourceModel.verifyArg(segment, "segment");

        visit_CALDoc_TextSegment_InlineTag_Link_WithContext_Helper(segment,
                options);
        return null;
    }

    /**
     * @param segment
     *            the source model element to be traversed
     * @param options
     *            unused argument
     * @return null
     */
    public Void visit_CALDoc_TextSegment_InlineTag_Link_TypeCons(
            CALDoc.TextSegment.InlineTag.Link.TypeCons segment, Options options) {

        SourceModel.verifyArg(segment, "segment");

        visit_CALDoc_TextSegment_InlineTag_Link_WithContext_Helper(segment,
                options);
        return null;
    }

    /**
     * @param segment
     *            the source model element to be traversed
     * @param options
     *            unused argument
     * @return null
     */
    public Void visit_CALDoc_TextSegment_InlineTag_Link_DataCons(
            CALDoc.TextSegment.InlineTag.Link.DataCons segment, Options options) {

        SourceModel.verifyArg(segment, "segment");

        visit_CALDoc_TextSegment_InlineTag_Link_WithContext_Helper(segment,
                options);
        return null;
    }

    /**
     * @param segment
     *            the source model element to be traversed
     * @param options
     *            unused argument
     * @return null
     */
    public Void visit_CALDoc_TextSegment_InlineTag_Link_TypeClass(
            CALDoc.TextSegment.InlineTag.Link.TypeClass segment, Options options) {

        SourceModel.verifyArg(segment, "segment");

        visit_CALDoc_TextSegment_InlineTag_Link_WithContext_Helper(segment,
                options);
        return null;
    }

    private void visit_CALDoc_TextSegment_InlineTag_Link_WithoutContext_Helper(
            CALDoc.TextSegment.InlineTag.Link.WithoutContext segment,
            Options info) {

        emitCalDocText("{@link ", info);

        beginAtomicCalDocItem();
        segment.getAbstractCrossReference().accept(this, info);
        sb.append("@}");
        endAtomicCalDocItem(info);
    }

    /**
     * @param segment
     *            the source model element to be traversed
     * @param options
     *            unused argument
     * @return null
     */
    public Void visit_CALDoc_TextSegment_InlineTag_Link_ConsNameWithoutContext(
            CALDoc.TextSegment.InlineTag.Link.ConsNameWithoutContext segment,
            Options options) {

        SourceModel.verifyArg(segment, "segment");

        visit_CALDoc_TextSegment_InlineTag_Link_WithoutContext_Helper(
                segment, options);
        return null;
    }

    /**
     * @param segment
     *            the source model element to be traversed
     * @param options
     *            unused argument
     * @return null
     */
    public Void visit_CALDoc_TextSegment_InlineTag_Link_FunctionWithoutContext(
            CALDoc.TextSegment.InlineTag.Link.FunctionWithoutContext segment,
            Options options) {

        SourceModel.verifyArg(segment, "segment");

        visit_CALDoc_TextSegment_InlineTag_Link_WithoutContext_Helper(
                segment, options);
        return null;
    }

    private void visit_CALDoc_TextSegment_InlineTag_WithTextBlockContent(
            CALDoc.TextSegment.InlineTag.InlineTagWithTextBlockContent segment,
            Options info) {

        emitCalDocText("{@" + segment.getTagName(), info);
        emitCalDocText(" ", info);
        segment.getContent().accept(this, info);

        emitCloseTag(info);
    }

    /**
     * @param segment
     *            the source model element to be traversed
     * @param options
     *            unused argument
     * @return null
     */
    public Void visit_CALDoc_TextSegment_InlineTag_TextFormatting_Emphasized(
            CALDoc.TextSegment.InlineTag.TextFormatting.Emphasized segment,
            Options options) {

        SourceModel.verifyArg(segment, "segment");

        visit_CALDoc_TextSegment_InlineTag_WithTextBlockContent(segment,
                options);
        return null;
    }

    /**
     * @param segment
     *            the source model element to be traversed
     * @param options
     *            unused argument
     * @return null
     */
    public Void visit_CALDoc_TextSegment_InlineTag_TextFormatting_StronglyEmphasized(
            CALDoc.TextSegment.InlineTag.TextFormatting.StronglyEmphasized segment,
            Options options) {

        SourceModel.verifyArg(segment, "segment");

        visit_CALDoc_TextSegment_InlineTag_WithTextBlockContent(segment,
                options);
        return null;
    }

    /**
     * @param segment
     *            the source model element to be traversed
     * @param options
     *            unused argument
     * @return null
     */
    public Void visit_CALDoc_TextSegment_InlineTag_TextFormatting_Superscript(
            CALDoc.TextSegment.InlineTag.TextFormatting.Superscript segment,
            Options options) {

        SourceModel.verifyArg(segment, "segment");

        visit_CALDoc_TextSegment_InlineTag_WithTextBlockContent(segment,
                options);
        return null;
    }

    /**
     * @param segment
     *            the source model element to be traversed
     * @param options
     *            unused argument
     * @return null
     */
    public Void visit_CALDoc_TextSegment_InlineTag_TextFormatting_Subscript(
            CALDoc.TextSegment.InlineTag.TextFormatting.Subscript segment,
            Options options) {

        SourceModel.verifyArg(segment, "segment");

        visit_CALDoc_TextSegment_InlineTag_WithTextBlockContent(segment,
                options);
        return null;
    }

    /**
     * @param segment
     *            the source model element to be traversed
     * @param options
     *            unused argument
     * @return null
     */
    public Void visit_CALDoc_TextSegment_InlineTag_Summary(
            CALDoc.TextSegment.InlineTag.Summary segment, Options options) {

        SourceModel.verifyArg(segment, "segment");

        visit_CALDoc_TextSegment_InlineTag_WithTextBlockContent(segment,
                options);
        return null;
    }

    private void visit_CALDoc_TextSegment_InlineTag_WithPreformattedContent(
            CALDoc.TextSegment.InlineTag.InlineTagWithPreformattedContent segment,
            Options info) {

        final String open = "{@" + segment.getTagName() + " ";
        final String close = "@}";
        StringBuilder codeBlock = new StringBuilder();

        SourceModelCALDocFormatter contentFormatter = new SourceModelCALDocFormatter(codeBlock);
        contentFormatter.isPreformatted = true;
        
        segment.getContent().accept(contentFormatter, info);

        // does the code item span more than one line?
        if (codeBlock.indexOf(EOL ) >= 0) {
            ensureOnNewLine();
            emitCalDocText(open, info);
            sb.append(codeBlock.toString().replaceAll("(?s)(" + EOLRegex + ") ", EOL));
            sb.append(close);
        } else {
            // no - make sure the open, text and close are one atomic item
            emitCalDocText(open + codeBlock.toString() + close, info);
        }
    }

    /**
     * @param segment
     *            the source model element to be traversed
     * @param options
     *            unused argument
     * @return null
     */
    public Void visit_CALDoc_TextSegment_InlineTag_Code(
            CALDoc.TextSegment.InlineTag.Code segment, Options options) {

        SourceModel.verifyArg(segment, "segment");

        visit_CALDoc_TextSegment_InlineTag_WithPreformattedContent(segment,
                options);
        return null;
    }

    private void visit_CALDoc_TextSegment_InlineTag_List(
            CALDoc.TextSegment.InlineTag.List list, Options info) {

        // start the ordered list on a new line if we're not already on one
        ensureOnNewLine();
        sb.append("{@").append(list.getTagName());
        
        indent += STANDARD_INDENT;                
        for (int i = 0; i < list.getNItems(); i++) {
            emitCalDocLine();
            list.getNthItem(i).accept(this, info);
        }
        indent -= STANDARD_INDENT;
        
        emitCalDocLine();
        sb.append("@}");

    }

    /**
     * @param list
     *            the source model element to be traversed
     * @param options
     *            unused argument
     * @return null
     */
    public Void visit_CALDoc_TextSegment_InlineTag_List_Unordered(
            CALDoc.TextSegment.InlineTag.List.Unordered list, Options options) {

        SourceModel.verifyArg(list, "list");

        visit_CALDoc_TextSegment_InlineTag_List(list, options);

        return null;
    }

    /**
     * @param list
     *            the source model element to be traversed
     * @param options
     *            unused argument
     * @return null
     */
    public Void visit_CALDoc_TextSegment_InlineTag_List_Ordered(
            CALDoc.TextSegment.InlineTag.List.Ordered list, Options options) {

        SourceModel.verifyArg(list, "list");

        visit_CALDoc_TextSegment_InlineTag_List(list, options);

        return null;
    }

    /**
     * @param item
     *            the source model element to be traversed
     * @param options
     *            unused argument
     * @return null
     */
    public Void visit_CALDoc_TextSegment_InlineTag_List_Item(
            CALDoc.TextSegment.InlineTag.List.Item item, Options options) {

        SourceModel.verifyArg(item, "item");

        Options info = options;

        emitCalDocText("{@item ", options);

        int itemStart = sb.length();

        indent += STANDARD_INDENT;
        item.getContent().accept(this, info);
        indent -= STANDARD_INDENT;

        // does the item span more than one line?
        if (sb.indexOf(EOL, itemStart) > 0 ) {
            ensureOnNewLine();
        }

        emitCloseTag(options);
        return null;
    }

    /**
     * @param block
     *            the source model element to be traversed
     * @param options
     *            unused argument
     * @return null
     */
    public Void visit_CALDoc_TextBlock(CALDoc.TextBlock block, Options options) {

        SourceModel.verifyArg(block, "block");

        for (int i = 0, nSegments = block.getNSegments(); i < nSegments; i++) {
            newLineRemoved = false;  
            block.getNthSegment(i).accept(this, options);
        }

        return null;
    }

    public void taggedBlockHelper(CALDoc.TaggedBlock taggedBlock, Options options) {
        ensureOnNewLine();
        
    }
    
    /**
     * @param authorBlock
     *            the source model element to be traversed
     * @param options
     *            unused argument
     * @return null
     */
    public Void visit_CALDoc_TaggedBlock_Author(
            CALDoc.TaggedBlock.Author authorBlock, Options options) {

        SourceModel.verifyArg(authorBlock, "authorBlock");

        ensureOnNewLine();
        sb.append(CALDoc.TaggedBlock.Author.AUTHOR_TAG ).append(" ");
        authorBlock.getTextBlock().accept(this, options);
       
       return null;
    }

    /**
     * @param deprecatedBlock
     *            the source model element to be traversed
     * @param options
     *            unused argument
     * @return null
     */
    public Void visit_CALDoc_TaggedBlock_Deprecated(
            CALDoc.TaggedBlock.Deprecated deprecatedBlock, Options options) {

        SourceModel.verifyArg(deprecatedBlock, "deprecatedBlock");

        ensureOnNewLine();
        sb.append(CALDoc.TaggedBlock.Deprecated.DEPRECATED_TAG).append(" ");
        indent += BLOCK_INDENT;
        deprecatedBlock.getTextBlock().accept(this, options);
        indent -= BLOCK_INDENT;
        
        return null;
    }

    /**
     * @param returnBlock
     *            the source model element to be traversed
     * @param options
     *            unused argument
     * @return null
     */
    public Void visit_CALDoc_TaggedBlock_Return(
            CALDoc.TaggedBlock.Return returnBlock, Options options) {

        SourceModel.verifyArg(returnBlock, "returnBlock");

        ensureOnNewLine();
        sb.append(CALDoc.TaggedBlock.Return.RETURN_TAG).append(" ");
        indent += BLOCK_INDENT;
        returnBlock.getTextBlock().accept(this, options);
        indent -= BLOCK_INDENT;
        
        return null;
    }

    /**
     * @param versionBlock
     *            the source model element to be traversed
     * @param options
     *            unused argument
     * @return null
     */
    public Void visit_CALDoc_TaggedBlock_Version(
            CALDoc.TaggedBlock.Version versionBlock, Options options) {

        SourceModel.verifyArg(versionBlock, "versionBlock");

        ensureOnNewLine();
        sb.append(CALDoc.TaggedBlock.Version.VERSION_TAG).append(" ");
        indent += BLOCK_INDENT;
        versionBlock.getTextBlock().accept(this, options);
        indent -= BLOCK_INDENT;
        
        return null;
    }

    /**
     * @param argBlock
     *            the source model element to be traversed
     * @param options
     *            unused argument
     * @return null
     */
    public Void visit_CALDoc_TaggedBlock_Arg(
            CALDoc.TaggedBlock.Arg argBlock, Options options) {

        SourceModel.verifyArg(argBlock, "argBlock");

        ensureOnNewLine();
        sb.append(CALDoc.TaggedBlock.Arg.ARG_TAG).append(' ')
                .append(argBlock.getArgName().getName().getCalSourceForm());

        indent += BLOCK_INDENT;
        argBlock.getTextBlock().accept(this, options);
        indent -= BLOCK_INDENT;

        return null;
    }

    /**
     * @param function
     *            the source model element to be traversed
     * @param options
     *            unused argument
     * @return null
     */
    public Void visit_CALDoc_TaggedBlock_See_Function(
            CALDoc.TaggedBlock.See.Function function, Options options) {

        SourceModel.verifyArg(function, "function");

        Options info = options;
        ensureOnNewLine();
        sb.append(CALDoc.TaggedBlock.See.SEE_TAG).append(
                " function = ");
        function.getNthFunctionName(0).accept(this, info);

        for (int i = 1, n = function.getNFunctionNames(); i < n; i++) {
            sb.append(", ");
            beginAtomicCalDocItem();
            function.getNthFunctionName(i).accept(this, info);
            endAtomicCalDocItem(options);
        }

        return null;
    }

    /**
     * @param module
     *            the source model element to be traversed
     * @param options
     *            unused argument
     * @return null
     */
    public Void visit_CALDoc_TaggedBlock_See_Module(
            CALDoc.TaggedBlock.See.Module module, Options options) {

        SourceModel.verifyArg(module, "module");

        Options info = options;

        ensureOnNewLine();
        sb.append(CALDoc.TaggedBlock.See.SEE_TAG).append(
                " module = ");
        module.getNthModuleName(0).accept(this, info);

        for (int i = 1, n = module.getNModuleNames(); i < n; i++) {
            sb.append(", ");
            beginAtomicCalDocItem();
            module.getNthModuleName(i).accept(this, info);
            endAtomicCalDocItem(options);
        }

        return null;
    }

    /**
     * @param typeCons
     *            the source model element to be traversed
     * @param options
     *            unused argument
     * @return null
     */
    public Void visit_CALDoc_TaggedBlock_See_TypeCons(
            CALDoc.TaggedBlock.See.TypeCons typeCons, Options options) {

        SourceModel.verifyArg(typeCons, "typeCons");

        Options info = options;

        ensureOnNewLine();
        sb.append(CALDoc.TaggedBlock.See.SEE_TAG).append(
                " typeConstructor = ");
        typeCons.getNthTypeConsName(0).accept(this, info);

        for (int i = 1, n = typeCons.getNTypeConsNames(); i < n; i++) {
            sb.append(", ");
            beginAtomicCalDocItem();
            typeCons.getNthTypeConsName(i).accept(this, info);
            endAtomicCalDocItem(options);
        }

        return null;
    }

    /**
     * @param dataCons
     *            the source model element to be traversed
     * @param options
     *            unused argument
     * @return null
     */
    public Void visit_CALDoc_TaggedBlock_See_DataCons(
            CALDoc.TaggedBlock.See.DataCons dataCons, Options options) {

        Options info = options;

        ensureOnNewLine();
        sb.append(CALDoc.TaggedBlock.See.SEE_TAG).append(
                " dataConstructor = ");
        dataCons.getNthDataConsName(0).accept(this, info);

        for (int i = 1, n = dataCons.getNDataConsNames(); i < n; i++) {
            sb.append(", ");
            beginAtomicCalDocItem();
            dataCons.getNthDataConsName(i).accept(this, info);
            endAtomicCalDocItem(options);
        }

        return null;
    }

    /**
     * @param typeClass
     *            the source model element to be traversed
     * @param options
     *            unused argument
     * @return null
     */
    public Void visit_CALDoc_TaggedBlock_See_TypeClass(
            CALDoc.TaggedBlock.See.TypeClass typeClass, Options options) {

        SourceModel.verifyArg(typeClass, "typeClass");

        Options info = options;

        ensureOnNewLine();
        sb.append(CALDoc.TaggedBlock.See.SEE_TAG).append(
                " typeClass = ");
        typeClass.getNthTypeClassName(0).accept(this, info);

        for (int i = 1, n = typeClass.getNTypeClassNames(); i < n; i++) {
            sb.append(", ");
            beginAtomicCalDocItem();
            typeClass.getNthTypeClassName(i).accept(this, info);
            endAtomicCalDocItem(options);
        }

        return null;
    }

    /**
     * @param reference
     *            the source model element to be traversed
     * @param options
     *            unused argument
     * @return null
     */
    public Void visit_CALDoc_CrossReference_Function(
            CALDoc.CrossReference.Function reference, Options options) {

        SourceModel.verifyArg(reference, "reference");

        if (reference.isChecked()) {
            reference.getName().accept(this, options);
        } else {
            sb.append('\"');
            reference.getName().accept(this, options);
            sb.append('\"');
        }

        return null;
    }

    /**
     * @param reference
     *            the source model element to be traversed
     * @param options
     *            unused argument
     * @return null
     */
    public Void visit_CALDoc_CrossReference_Module(
            CALDoc.CrossReference.Module reference, Options options) {

        SourceModel.verifyArg(reference, "reference");

        if (reference.isChecked()) {
            reference.getName().accept(this, options);
        } else {
            sb.append('\"');
            reference.getName().accept(this, options);
            sb.append('\"');
        }
        return null;
    }

    /**
     * @param reference
     *            the source model element to be traversed
     * @param options
     *            unused argument
     * @return null
     */
    public Void visit_CALDoc_CrossReference_TypeCons(
            CALDoc.CrossReference.TypeCons reference, Options options) {

        SourceModel.verifyArg(reference, "reference");

        if (reference.isChecked()) {
            reference.getName().accept(this, options);
        } else {
            sb.append('\"');
            reference.getName().accept(this, options);
            sb.append('\"');
        }
        return null;
    }

    /**
     * @param reference
     *            the source model element to be traversed
     * @param options
     *            unused argument
     * @return null
     */
    public Void visit_CALDoc_CrossReference_DataCons(
            CALDoc.CrossReference.DataCons reference, Options options) {

        SourceModel.verifyArg(reference, "reference");

        if (reference.isChecked()) {
            reference.getName().accept(this, options);
        } else {
            sb.append('\"');
            reference.getName().accept(this, options);
            sb.append('\"');
        }
        return null;
    }

    /**
     * @param reference
     *            the source model element to be traversed
     * @param options
     *            the formatting options
     * @return null
     */
    public Void visit_CALDoc_CrossReference_TypeClass(
            CALDoc.CrossReference.TypeClass reference, Options options) {

        SourceModel.verifyArg(reference, "reference");

        if (reference.isChecked()) {
            reference.getName().accept(this, options);
        } else {
            sb.append('\"');
            reference.getName().accept(this, options);
            sb.append('\"');
        }
        return null;
    }

    /**
     * @param reference
     *            the source model element to be traversed
     * @param options
     *            the formatting options
     * @return null
     */
    public Void visit_CALDoc_CrossReference_WithoutContextCons(
            CALDoc.CrossReference.WithoutContextCons reference, Options options) {

        SourceModel.verifyArg(reference, "reference");

        if (reference.isChecked()) {
            reference.getName().accept(this, options);
        } else {
            sb.append('\"');
            reference.getName().accept(this, options);
            sb.append('\"');
        }
        return null;
    }

    /**
     * @param seeBlock
     *            the source model element to be traversed
     * @param options
     *            the formatting options
     * @return null
     */
    public Void visit_CALDoc_TaggedBlock_See_WithoutContext(
            CALDoc.TaggedBlock.See.WithoutContext seeBlock, Options options) {

        SourceModel.verifyArg(seeBlock, "seeBlock");

        ensureOnNewLine();
        sb.append(CALDoc.TaggedBlock.See.SEE_TAG).append(" ");
        seeBlock.getNthReferencedName(0).accept(this, options);

        for (int i = 1, n = seeBlock.getNReferencedNames(); i < n; i++) {
            sb.append(", ");

            beginAtomicCalDocItem();
            seeBlock.getNthReferencedName(i).accept(this, options);
            endAtomicCalDocItem(options);

        }
        return null;
    }

 
}
