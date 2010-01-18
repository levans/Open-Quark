/*
 * <!--
 *  
 * **************************************************************
 * This Java source has been automatically generated.
 * MODIFICATIONS TO THIS SOURCE MAY BE OVERWRITTEN - DO NOT MODIFY THIS FILE
 * **************************************************************
 *  
 *  
 * This file (CAL_Drawing.java)
 * was generated from CAL module: Cal.Experimental.Graphics.Drawing.
 * The constants and methods provided are intended to facilitate accessing the
 * Cal.Experimental.Graphics.Drawing module from Java code.
 *  
 * Creation date: Wed Oct 17 14:59:38 PDT 2007
 * --!>
 *  
 */

package org.openquark.cal.module.Cal.Experimental.Graphics;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.SourceModel;

/**
 * This experimental module contains functions which give access to
 * Java2D functionality from CAL.
 * There are functions to draw text, shapes, images, etc... to a graphics device (a Java Graphics2D).
 * Functions also exist to perform any drawing with a transformation
 * applied (scaling, shifting, etc...).
 * @author Richard Webster
 */
public final class CAL_Drawing {
	public static final ModuleName MODULE_NAME = 
		ModuleName.make("Cal.Experimental.Graphics.Drawing");

	/**
	 * This inner class (TypeConstructors) contains constants
	 * and methods related to binding to CAL TypeConstructors in the Cal.Experimental.Graphics.Drawing module.
	 */
	public static final class TypeConstructors {
		/**
		 * A font for printing on a graphics device.
		 */
		public static final QualifiedName Font = 
			QualifiedName.make(CAL_Drawing.MODULE_NAME, "Font");

		/**
		 * The graphics device to which drawing can be done.
		 */
		public static final QualifiedName Graphics = 
			QualifiedName.make(CAL_Drawing.MODULE_NAME, "Graphics");

		/**
		 * A icon image.
		 */
		public static final QualifiedName Icon = 
			QualifiedName.make(CAL_Drawing.MODULE_NAME, "Icon");

		/**
		 * An image.
		 */
		public static final QualifiedName Image = 
			QualifiedName.make(CAL_Drawing.MODULE_NAME, "Image");

		/**
		 * A paint (i.e. fill pattern) for coloring a graphics device.
		 */
		public static final QualifiedName Paint = 
			QualifiedName.make(CAL_Drawing.MODULE_NAME, "Paint");

		/**
		 * Rendering Hints.
		 */
		public static final QualifiedName RenderingHint = 
			QualifiedName.make(CAL_Drawing.MODULE_NAME, "RenderingHint");

		/** Name binding for TypeConsApp: RenderingHintKey. */
		public static final QualifiedName RenderingHintKey = 
			QualifiedName.make(CAL_Drawing.MODULE_NAME, "RenderingHintKey");

		/** Name binding for TypeConsApp: RenderingHintValue. */
		public static final QualifiedName RenderingHintValue = 
			QualifiedName.make(CAL_Drawing.MODULE_NAME, "RenderingHintValue");

		/**
		 * Shapes which can be drawn.
		 */
		public static final QualifiedName Shape = 
			QualifiedName.make(CAL_Drawing.MODULE_NAME, "Shape");

		/**
		 * A stroke (i.e. line pattern) for drawing on a graphics device.
		 */
		public static final QualifiedName Stroke = 
			QualifiedName.make(CAL_Drawing.MODULE_NAME, "Stroke");

	}
	/**
	 * This inner class (Functions) contains constants
	 * and methods related to binding to CAL functions in the Cal.Experimental.Graphics.Drawing module.
	 */
	public static final class Functions {
		/**
		 * A stroke with default attributes.
		 * @return (CAL type: <code>Cal.Experimental.Graphics.Drawing.Stroke</code>) 
		 */
		public static final SourceModel.Expr defaultStroke() {
			return SourceModel.Expr.Var.make(Functions.defaultStroke);
		}

		/**
		 * Name binding for function: defaultStroke.
		 * @see #defaultStroke()
		 */
		public static final QualifiedName defaultStroke = 
			QualifiedName.make(CAL_Drawing.MODULE_NAME, "defaultStroke");

		/**
		 * Displays a dialog showing the results of the specified drawing function.
		 * @param caption (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the dialog caption
		 * @param drawFn (CAL type: <code>Cal.Experimental.Graphics.Drawing.Graphics -> Cal.Experimental.Graphics.Drawing.Graphics</code>)
		 *          the function to draw the dialog contents
		 * @return (CAL type: <code>()</code>) 
		 */
		public static final SourceModel.Expr displayCustomDrawDialog(SourceModel.Expr caption, SourceModel.Expr drawFn) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.displayCustomDrawDialog), caption, drawFn});
		}

		/**
		 * @see #displayCustomDrawDialog(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param caption
		 * @param drawFn
		 * @return the SourceModel.Expr representing an application of displayCustomDrawDialog
		 */
		public static final SourceModel.Expr displayCustomDrawDialog(java.lang.String caption, SourceModel.Expr drawFn) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.displayCustomDrawDialog), SourceModel.Expr.makeStringValue(caption), drawFn});
		}

		/**
		 * Name binding for function: displayCustomDrawDialog.
		 * @see #displayCustomDrawDialog(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName displayCustomDrawDialog = 
			QualifiedName.make(
				CAL_Drawing.MODULE_NAME, 
				"displayCustomDrawDialog");

		/**
		 * Displays the image in a dialog.
		 * @param caption (CAL type: <code>Cal.Core.Prelude.String</code>)
		 *          the dialog caption
		 * @param image (CAL type: <code>Cal.Experimental.Graphics.Drawing.Image</code>)
		 *          the image to be displayed
		 * @return (CAL type: <code>()</code>) 
		 */
		public static final SourceModel.Expr displayImage(SourceModel.Expr caption, SourceModel.Expr image) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.displayImage), caption, image});
		}

		/**
		 * @see #displayImage(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param caption
		 * @param image
		 * @return the SourceModel.Expr representing an application of displayImage
		 */
		public static final SourceModel.Expr displayImage(java.lang.String caption, SourceModel.Expr image) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.displayImage), SourceModel.Expr.makeStringValue(caption), image});
		}

		/**
		 * Name binding for function: displayImage.
		 * @see #displayImage(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName displayImage = 
			QualifiedName.make(CAL_Drawing.MODULE_NAME, "displayImage");

		/**
		 * Draws an entire image at full size at the specified graphics coordinates.
		 * @param image (CAL type: <code>Cal.Experimental.Graphics.Drawing.Image</code>)
		 * @param x (CAL type: <code>Cal.Core.Prelude.Double</code>)
		 * @param y (CAL type: <code>Cal.Core.Prelude.Double</code>)
		 * @param graphics (CAL type: <code>Cal.Experimental.Graphics.Drawing.Graphics</code>)
		 * @return (CAL type: <code>Cal.Experimental.Graphics.Drawing.Graphics</code>) 
		 */
		public static final SourceModel.Expr drawImage(SourceModel.Expr image, SourceModel.Expr x, SourceModel.Expr y, SourceModel.Expr graphics) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.drawImage), image, x, y, graphics});
		}

		/**
		 * @see #drawImage(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param image
		 * @param x
		 * @param y
		 * @param graphics
		 * @return the SourceModel.Expr representing an application of drawImage
		 */
		public static final SourceModel.Expr drawImage(SourceModel.Expr image, double x, double y, SourceModel.Expr graphics) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.drawImage), image, SourceModel.Expr.makeDoubleValue(x), SourceModel.Expr.makeDoubleValue(y), graphics});
		}

		/**
		 * Name binding for function: drawImage.
		 * @see #drawImage(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName drawImage = 
			QualifiedName.make(CAL_Drawing.MODULE_NAME, "drawImage");

		/**
		 * Draws an entire image with the specified size and coordinates.
		 * @param image (CAL type: <code>Cal.Experimental.Graphics.Drawing.Image</code>)
		 * @param x (CAL type: <code>Cal.Core.Prelude.Double</code>)
		 * @param y (CAL type: <code>Cal.Core.Prelude.Double</code>)
		 * @param w (CAL type: <code>Cal.Core.Prelude.Double</code>)
		 * @param h (CAL type: <code>Cal.Core.Prelude.Double</code>)
		 * @param graphics (CAL type: <code>Cal.Experimental.Graphics.Drawing.Graphics</code>)
		 * @return (CAL type: <code>Cal.Experimental.Graphics.Drawing.Graphics</code>) 
		 */
		public static final SourceModel.Expr drawImageWithSize(SourceModel.Expr image, SourceModel.Expr x, SourceModel.Expr y, SourceModel.Expr w, SourceModel.Expr h, SourceModel.Expr graphics) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.drawImageWithSize), image, x, y, w, h, graphics});
		}

		/**
		 * @see #drawImageWithSize(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param image
		 * @param x
		 * @param y
		 * @param w
		 * @param h
		 * @param graphics
		 * @return the SourceModel.Expr representing an application of drawImageWithSize
		 */
		public static final SourceModel.Expr drawImageWithSize(SourceModel.Expr image, double x, double y, double w, double h, SourceModel.Expr graphics) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.drawImageWithSize), image, SourceModel.Expr.makeDoubleValue(x), SourceModel.Expr.makeDoubleValue(y), SourceModel.Expr.makeDoubleValue(w), SourceModel.Expr.makeDoubleValue(h), graphics});
		}

		/**
		 * Name binding for function: drawImageWithSize.
		 * @see #drawImageWithSize(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName drawImageWithSize = 
			QualifiedName.make(CAL_Drawing.MODULE_NAME, "drawImageWithSize");

		/**
		 * Applies the specified drawing to an icon.
		 * The modified icon is returned.
		 * The width and height of the icon will be passed to the drawing function.
		 * The size of the resulting icon will be the same as the original.
		 * @param drawFn (CAL type: <code>Cal.Core.Prelude.Int -> Cal.Core.Prelude.Int -> Cal.Experimental.Graphics.Drawing.Graphics -> Cal.Experimental.Graphics.Drawing.Graphics</code>)
		 * @param icon (CAL type: <code>Cal.Experimental.Graphics.Drawing.Icon</code>)
		 * @return (CAL type: <code>Cal.Experimental.Graphics.Drawing.Icon</code>) 
		 */
		public static final SourceModel.Expr drawOnIcon(SourceModel.Expr drawFn, SourceModel.Expr icon) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.drawOnIcon), drawFn, icon});
		}

		/**
		 * Name binding for function: drawOnIcon.
		 * @see #drawOnIcon(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName drawOnIcon = 
			QualifiedName.make(CAL_Drawing.MODULE_NAME, "drawOnIcon");

		/**
		 * Performs drawing with the output rotated about the specified point.
		 * The rotation angle is in radians.
		 * @param rotationAngle (CAL type: <code>Cal.Core.Prelude.Double</code>)
		 * @param x (CAL type: <code>Cal.Core.Prelude.Double</code>)
		 * @param y (CAL type: <code>Cal.Core.Prelude.Double</code>)
		 * @param drawingFn (CAL type: <code>Cal.Experimental.Graphics.Drawing.Graphics -> Cal.Experimental.Graphics.Drawing.Graphics</code>)
		 * @param graphics (CAL type: <code>Cal.Experimental.Graphics.Drawing.Graphics</code>)
		 * @return (CAL type: <code>Cal.Experimental.Graphics.Drawing.Graphics</code>) 
		 */
		public static final SourceModel.Expr drawRotated(SourceModel.Expr rotationAngle, SourceModel.Expr x, SourceModel.Expr y, SourceModel.Expr drawingFn, SourceModel.Expr graphics) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.drawRotated), rotationAngle, x, y, drawingFn, graphics});
		}

		/**
		 * @see #drawRotated(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param rotationAngle
		 * @param x
		 * @param y
		 * @param drawingFn
		 * @param graphics
		 * @return the SourceModel.Expr representing an application of drawRotated
		 */
		public static final SourceModel.Expr drawRotated(double rotationAngle, double x, double y, SourceModel.Expr drawingFn, SourceModel.Expr graphics) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.drawRotated), SourceModel.Expr.makeDoubleValue(rotationAngle), SourceModel.Expr.makeDoubleValue(x), SourceModel.Expr.makeDoubleValue(y), drawingFn, graphics});
		}

		/**
		 * Name binding for function: drawRotated.
		 * @see #drawRotated(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName drawRotated = 
			QualifiedName.make(CAL_Drawing.MODULE_NAME, "drawRotated");

		/**
		 * Performs drawing with the output scaled in the x and y directions.
		 * @param xScaling (CAL type: <code>Cal.Core.Prelude.Double</code>)
		 * @param yScaling (CAL type: <code>Cal.Core.Prelude.Double</code>)
		 * @param drawingFn (CAL type: <code>Cal.Experimental.Graphics.Drawing.Graphics -> Cal.Experimental.Graphics.Drawing.Graphics</code>)
		 * @param graphics (CAL type: <code>Cal.Experimental.Graphics.Drawing.Graphics</code>)
		 * @return (CAL type: <code>Cal.Experimental.Graphics.Drawing.Graphics</code>) 
		 */
		public static final SourceModel.Expr drawScaled(SourceModel.Expr xScaling, SourceModel.Expr yScaling, SourceModel.Expr drawingFn, SourceModel.Expr graphics) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.drawScaled), xScaling, yScaling, drawingFn, graphics});
		}

		/**
		 * @see #drawScaled(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param xScaling
		 * @param yScaling
		 * @param drawingFn
		 * @param graphics
		 * @return the SourceModel.Expr representing an application of drawScaled
		 */
		public static final SourceModel.Expr drawScaled(double xScaling, double yScaling, SourceModel.Expr drawingFn, SourceModel.Expr graphics) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.drawScaled), SourceModel.Expr.makeDoubleValue(xScaling), SourceModel.Expr.makeDoubleValue(yScaling), drawingFn, graphics});
		}

		/**
		 * Name binding for function: drawScaled.
		 * @see #drawScaled(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName drawScaled = 
			QualifiedName.make(CAL_Drawing.MODULE_NAME, "drawScaled");

		/**
		 * Draws the specified shape.
		 * @param paint (CAL type: <code>Cal.Experimental.Graphics.Drawing.Paint</code>)
		 * @param stroke (CAL type: <code>Cal.Experimental.Graphics.Drawing.Stroke</code>)
		 * @param shape (CAL type: <code>Cal.Experimental.Graphics.Drawing.Shape</code>)
		 * @param graphics (CAL type: <code>Cal.Experimental.Graphics.Drawing.Graphics</code>)
		 * @return (CAL type: <code>Cal.Experimental.Graphics.Drawing.Graphics</code>) 
		 */
		public static final SourceModel.Expr drawShape(SourceModel.Expr paint, SourceModel.Expr stroke, SourceModel.Expr shape, SourceModel.Expr graphics) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.drawShape), paint, stroke, shape, graphics});
		}

		/**
		 * Name binding for function: drawShape.
		 * @see #drawShape(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName drawShape = 
			QualifiedName.make(CAL_Drawing.MODULE_NAME, "drawShape");

		/**
		 * Draws the specified shapes.
		 * @param paint (CAL type: <code>Cal.Experimental.Graphics.Drawing.Paint</code>)
		 * @param stroke (CAL type: <code>Cal.Experimental.Graphics.Drawing.Stroke</code>)
		 * @param shapes (CAL type: <code>[Cal.Experimental.Graphics.Drawing.Shape]</code>)
		 * @param graphics (CAL type: <code>Cal.Experimental.Graphics.Drawing.Graphics</code>)
		 * @return (CAL type: <code>Cal.Experimental.Graphics.Drawing.Graphics</code>) 
		 */
		public static final SourceModel.Expr drawShapes(SourceModel.Expr paint, SourceModel.Expr stroke, SourceModel.Expr shapes, SourceModel.Expr graphics) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.drawShapes), paint, stroke, shapes, graphics});
		}

		/**
		 * Name binding for function: drawShapes.
		 * @see #drawShapes(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName drawShapes = 
			QualifiedName.make(CAL_Drawing.MODULE_NAME, "drawShapes");

		/**
		 * Draws text to the graphics.
		 * @param paint (CAL type: <code>Cal.Experimental.Graphics.Drawing.Paint</code>)
		 * @param font (CAL type: <code>Cal.Experimental.Graphics.Drawing.Font</code>)
		 * @param text (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @param x (CAL type: <code>Cal.Core.Prelude.Num a => a</code>)
		 * @param y (CAL type: <code>Cal.Core.Prelude.Num b => b</code>)
		 * @param graphics (CAL type: <code>Cal.Experimental.Graphics.Drawing.Graphics</code>)
		 * @return (CAL type: <code>Cal.Experimental.Graphics.Drawing.Graphics</code>) 
		 */
		public static final SourceModel.Expr drawText(SourceModel.Expr paint, SourceModel.Expr font, SourceModel.Expr text, SourceModel.Expr x, SourceModel.Expr y, SourceModel.Expr graphics) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.drawText), paint, font, text, x, y, graphics});
		}

		/**
		 * @see #drawText(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param paint
		 * @param font
		 * @param text
		 * @param x
		 * @param y
		 * @param graphics
		 * @return the SourceModel.Expr representing an application of drawText
		 */
		public static final SourceModel.Expr drawText(SourceModel.Expr paint, SourceModel.Expr font, java.lang.String text, SourceModel.Expr x, SourceModel.Expr y, SourceModel.Expr graphics) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.drawText), paint, font, SourceModel.Expr.makeStringValue(text), x, y, graphics});
		}

		/**
		 * Name binding for function: drawText.
		 * @see #drawText(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName drawText = 
			QualifiedName.make(CAL_Drawing.MODULE_NAME, "drawText");

		/**
		 * Creates an image of the specified size with a white background and performs some drawing on it.
		 * @param imageHeight (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 * @param imageWidth (CAL type: <code>Cal.Core.Prelude.Int</code>)
		 * @param drawFn (CAL type: <code>Cal.Experimental.Graphics.Drawing.Graphics -> Cal.Experimental.Graphics.Drawing.Graphics</code>)
		 * @return (CAL type: <code>Cal.Experimental.Graphics.Drawing.Image</code>) 
		 */
		public static final SourceModel.Expr drawToImage(SourceModel.Expr imageHeight, SourceModel.Expr imageWidth, SourceModel.Expr drawFn) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.drawToImage), imageHeight, imageWidth, drawFn});
		}

		/**
		 * @see #drawToImage(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param imageHeight
		 * @param imageWidth
		 * @param drawFn
		 * @return the SourceModel.Expr representing an application of drawToImage
		 */
		public static final SourceModel.Expr drawToImage(int imageHeight, int imageWidth, SourceModel.Expr drawFn) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.drawToImage), SourceModel.Expr.makeIntValue(imageHeight), SourceModel.Expr.makeIntValue(imageWidth), drawFn});
		}

		/**
		 * Name binding for function: drawToImage.
		 * @see #drawToImage(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName drawToImage = 
			QualifiedName.make(CAL_Drawing.MODULE_NAME, "drawToImage");

		/**
		 * Performs drawing with the output translated in the x and y directions.
		 * @param xShift (CAL type: <code>Cal.Core.Prelude.Double</code>)
		 * @param yShift (CAL type: <code>Cal.Core.Prelude.Double</code>)
		 * @param drawingFn (CAL type: <code>Cal.Experimental.Graphics.Drawing.Graphics -> Cal.Experimental.Graphics.Drawing.Graphics</code>)
		 * @param graphics (CAL type: <code>Cal.Experimental.Graphics.Drawing.Graphics</code>)
		 * @return (CAL type: <code>Cal.Experimental.Graphics.Drawing.Graphics</code>) 
		 */
		public static final SourceModel.Expr drawTranslated(SourceModel.Expr xShift, SourceModel.Expr yShift, SourceModel.Expr drawingFn, SourceModel.Expr graphics) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.drawTranslated), xShift, yShift, drawingFn, graphics});
		}

		/**
		 * @see #drawTranslated(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param xShift
		 * @param yShift
		 * @param drawingFn
		 * @param graphics
		 * @return the SourceModel.Expr representing an application of drawTranslated
		 */
		public static final SourceModel.Expr drawTranslated(double xShift, double yShift, SourceModel.Expr drawingFn, SourceModel.Expr graphics) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.drawTranslated), SourceModel.Expr.makeDoubleValue(xShift), SourceModel.Expr.makeDoubleValue(yShift), drawingFn, graphics});
		}

		/**
		 * Name binding for function: drawTranslated.
		 * @see #drawTranslated(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName drawTranslated = 
			QualifiedName.make(CAL_Drawing.MODULE_NAME, "drawTranslated");

		/**
		 * Performs drawing with a clipping shape.
		 * @param clippingShape (CAL type: <code>Cal.Experimental.Graphics.Drawing.Shape</code>)
		 * @param drawingFn (CAL type: <code>Cal.Experimental.Graphics.Drawing.Graphics -> Cal.Experimental.Graphics.Drawing.Graphics</code>)
		 * @param graphics (CAL type: <code>Cal.Experimental.Graphics.Drawing.Graphics</code>)
		 * @return (CAL type: <code>Cal.Experimental.Graphics.Drawing.Graphics</code>) 
		 */
		public static final SourceModel.Expr drawWithClipping(SourceModel.Expr clippingShape, SourceModel.Expr drawingFn, SourceModel.Expr graphics) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.drawWithClipping), clippingShape, drawingFn, graphics});
		}

		/**
		 * Name binding for function: drawWithClipping.
		 * @see #drawWithClipping(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName drawWithClipping = 
			QualifiedName.make(CAL_Drawing.MODULE_NAME, "drawWithClipping");

		/**
		 * Performs drawing with the specified rendering hints set.
		 * @param renderingHints (CAL type: <code>[Cal.Experimental.Graphics.Drawing.RenderingHint]</code>)
		 * @param drawingFn (CAL type: <code>Cal.Experimental.Graphics.Drawing.Graphics -> Cal.Experimental.Graphics.Drawing.Graphics</code>)
		 * @param graphics (CAL type: <code>Cal.Experimental.Graphics.Drawing.Graphics</code>)
		 * @return (CAL type: <code>Cal.Experimental.Graphics.Drawing.Graphics</code>) 
		 */
		public static final SourceModel.Expr drawWithRenderingHints(SourceModel.Expr renderingHints, SourceModel.Expr drawingFn, SourceModel.Expr graphics) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.drawWithRenderingHints), renderingHints, drawingFn, graphics});
		}

		/**
		 * Name binding for function: drawWithRenderingHints.
		 * @see #drawWithRenderingHints(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName drawWithRenderingHints = 
			QualifiedName.make(
				CAL_Drawing.MODULE_NAME, 
				"drawWithRenderingHints");

		/**
		 * Fills the interior of the specified shape.
		 * @param paint (CAL type: <code>Cal.Experimental.Graphics.Drawing.Paint</code>)
		 * @param shape (CAL type: <code>Cal.Experimental.Graphics.Drawing.Shape</code>)
		 * @param graphics (CAL type: <code>Cal.Experimental.Graphics.Drawing.Graphics</code>)
		 * @return (CAL type: <code>Cal.Experimental.Graphics.Drawing.Graphics</code>) 
		 */
		public static final SourceModel.Expr fillShape(SourceModel.Expr paint, SourceModel.Expr shape, SourceModel.Expr graphics) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fillShape), paint, shape, graphics});
		}

		/**
		 * Name binding for function: fillShape.
		 * @see #fillShape(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName fillShape = 
			QualifiedName.make(CAL_Drawing.MODULE_NAME, "fillShape");

		/**
		 * Fills the interior of the specified shapes.
		 * @param paint (CAL type: <code>Cal.Experimental.Graphics.Drawing.Paint</code>)
		 * @param shapes (CAL type: <code>[Cal.Experimental.Graphics.Drawing.Shape]</code>)
		 * @param graphics (CAL type: <code>Cal.Experimental.Graphics.Drawing.Graphics</code>)
		 * @return (CAL type: <code>Cal.Experimental.Graphics.Drawing.Graphics</code>) 
		 */
		public static final SourceModel.Expr fillShapes(SourceModel.Expr paint, SourceModel.Expr shapes, SourceModel.Expr graphics) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.fillShapes), paint, shapes, graphics});
		}

		/**
		 * Name binding for function: fillShapes.
		 * @see #fillShapes(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName fillShapes = 
			QualifiedName.make(CAL_Drawing.MODULE_NAME, "fillShapes");

		/**
		 * Constructs a font from the specified info.
		 * @param arg_1 (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @param arg_2 (CAL type: <code>Cal.Core.Prelude.Double</code>)
		 * @param arg_3 (CAL type: <code>Cal.Core.Prelude.Boolean</code>)
		 * @param arg_4 (CAL type: <code>Cal.Core.Prelude.Boolean</code>)
		 * @return (CAL type: <code>Cal.Experimental.Graphics.Drawing.Font</code>) 
		 */
		public static final SourceModel.Expr font(SourceModel.Expr arg_1, SourceModel.Expr arg_2, SourceModel.Expr arg_3, SourceModel.Expr arg_4) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.font), arg_1, arg_2, arg_3, arg_4});
		}

		/**
		 * @see #font(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param arg_1
		 * @param arg_2
		 * @param arg_3
		 * @param arg_4
		 * @return the SourceModel.Expr representing an application of font
		 */
		public static final SourceModel.Expr font(java.lang.String arg_1, double arg_2, boolean arg_3, boolean arg_4) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.font), SourceModel.Expr.makeStringValue(arg_1), SourceModel.Expr.makeDoubleValue(arg_2), SourceModel.Expr.makeBooleanValue(arg_3), SourceModel.Expr.makeBooleanValue(arg_4)});
		}

		/**
		 * Name binding for function: font.
		 * @see #font(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName font = 
			QualifiedName.make(CAL_Drawing.MODULE_NAME, "font");

		/**
		 * Default rendering hints for high quality output.
		 * @return (CAL type: <code>[Cal.Experimental.Graphics.Drawing.RenderingHint]</code>) 
		 */
		public static final SourceModel.Expr highQualityRenderingHints() {
			return 
				SourceModel.Expr.Var.make(Functions.highQualityRenderingHints);
		}

		/**
		 * Name binding for function: highQualityRenderingHints.
		 * @see #highQualityRenderingHints()
		 */
		public static final QualifiedName highQualityRenderingHints = 
			QualifiedName.make(
				CAL_Drawing.MODULE_NAME, 
				"highQualityRenderingHints");

		/**
		 * Default rendering hints for faster drawing.
		 * @return (CAL type: <code>[Cal.Experimental.Graphics.Drawing.RenderingHint]</code>) 
		 */
		public static final SourceModel.Expr highSpeedRenderingHints() {
			return SourceModel.Expr.Var.make(Functions.highSpeedRenderingHints);
		}

		/**
		 * Name binding for function: highSpeedRenderingHints.
		 * @see #highSpeedRenderingHints()
		 */
		public static final QualifiedName highSpeedRenderingHints = 
			QualifiedName.make(
				CAL_Drawing.MODULE_NAME, 
				"highSpeedRenderingHints");

		/**
		 * Returns whether the specified rectangle might intersect the clipping rectangle in the graphics.
		 * @param graphics (CAL type: <code>Cal.Experimental.Graphics.Drawing.Graphics</code>)
		 * @param x (CAL type: <code>Cal.Core.Prelude.Double</code>)
		 * @param y (CAL type: <code>Cal.Core.Prelude.Double</code>)
		 * @param width (CAL type: <code>Cal.Core.Prelude.Double</code>)
		 * @param height (CAL type: <code>Cal.Core.Prelude.Double</code>)
		 * @return (CAL type: <code>Cal.Core.Prelude.Boolean</code>) 
		 */
		public static final SourceModel.Expr hitClip(SourceModel.Expr graphics, SourceModel.Expr x, SourceModel.Expr y, SourceModel.Expr width, SourceModel.Expr height) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.hitClip), graphics, x, y, width, height});
		}

		/**
		 * @see #hitClip(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param graphics
		 * @param x
		 * @param y
		 * @param width
		 * @param height
		 * @return the SourceModel.Expr representing an application of hitClip
		 */
		public static final SourceModel.Expr hitClip(SourceModel.Expr graphics, double x, double y, double width, double height) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.hitClip), graphics, SourceModel.Expr.makeDoubleValue(x), SourceModel.Expr.makeDoubleValue(y), SourceModel.Expr.makeDoubleValue(width), SourceModel.Expr.makeDoubleValue(height)});
		}

		/**
		 * Name binding for function: hitClip.
		 * @see #hitClip(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName hitClip = 
			QualifiedName.make(CAL_Drawing.MODULE_NAME, "hitClip");

		/**
		 * Loads an icon from the specified file name.
		 * @param fileName (CAL type: <code>Cal.IO.File.FileName</code>)
		 * @return (CAL type: <code>Cal.Experimental.Graphics.Drawing.Icon</code>) 
		 */
		public static final SourceModel.Expr iconFromFile(SourceModel.Expr fileName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.iconFromFile), fileName});
		}

		/**
		 * Name binding for function: iconFromFile.
		 * @see #iconFromFile(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName iconFromFile = 
			QualifiedName.make(CAL_Drawing.MODULE_NAME, "iconFromFile");

		/**
		 * Loads an icon from the 'Resources' folder in the classpath.
		 * The .gif extension will be added automatically.
		 * @param iconResourceName (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @return (CAL type: <code>Cal.Experimental.Graphics.Drawing.Icon</code>) 
		 */
		public static final SourceModel.Expr iconFromResource(SourceModel.Expr iconResourceName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.iconFromResource), iconResourceName});
		}

		/**
		 * @see #iconFromResource(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param iconResourceName
		 * @return the SourceModel.Expr representing an application of iconFromResource
		 */
		public static final SourceModel.Expr iconFromResource(java.lang.String iconResourceName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.iconFromResource), SourceModel.Expr.makeStringValue(iconResourceName)});
		}

		/**
		 * Name binding for function: iconFromResource.
		 * @see #iconFromResource(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName iconFromResource = 
			QualifiedName.make(CAL_Drawing.MODULE_NAME, "iconFromResource");

		/**
		 * Helper binding method for function: iconHeight. 
		 * @param icon
		 * @return the SourceModule.expr representing an application of iconHeight
		 */
		public static final SourceModel.Expr iconHeight(SourceModel.Expr icon) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.iconHeight), icon});
		}

		/**
		 * Name binding for function: iconHeight.
		 * @see #iconHeight(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName iconHeight = 
			QualifiedName.make(CAL_Drawing.MODULE_NAME, "iconHeight");

		/**
		 * Helper binding method for function: iconWidth. 
		 * @param icon
		 * @return the SourceModule.expr representing an application of iconWidth
		 */
		public static final SourceModel.Expr iconWidth(SourceModel.Expr icon) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.iconWidth), icon});
		}

		/**
		 * Name binding for function: iconWidth.
		 * @see #iconWidth(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName iconWidth = 
			QualifiedName.make(CAL_Drawing.MODULE_NAME, "iconWidth");

		/**
		 * Loads an image from the 'Resources' folder in the classpath.
		 * The resource name should include the file extension where appropriate.
		 * @param imageResourceName (CAL type: <code>Cal.Core.Prelude.String</code>)
		 * @return (CAL type: <code>Cal.Experimental.Graphics.Drawing.Image</code>) 
		 */
		public static final SourceModel.Expr imageFromResource(SourceModel.Expr imageResourceName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.imageFromResource), imageResourceName});
		}

		/**
		 * @see #imageFromResource(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param imageResourceName
		 * @return the SourceModel.Expr representing an application of imageFromResource
		 */
		public static final SourceModel.Expr imageFromResource(java.lang.String imageResourceName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.imageFromResource), SourceModel.Expr.makeStringValue(imageResourceName)});
		}

		/**
		 * Name binding for function: imageFromResource.
		 * @see #imageFromResource(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName imageFromResource = 
			QualifiedName.make(CAL_Drawing.MODULE_NAME, "imageFromResource");

		/**
		 * Helper binding method for function: imageHeight. 
		 * @param image
		 * @return the SourceModule.expr representing an application of imageHeight
		 */
		public static final SourceModel.Expr imageHeight(SourceModel.Expr image) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.imageHeight), image});
		}

		/**
		 * Name binding for function: imageHeight.
		 * @see #imageHeight(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName imageHeight = 
			QualifiedName.make(CAL_Drawing.MODULE_NAME, "imageHeight");

		/**
		 * Helper binding method for function: imageWidth. 
		 * @param image
		 * @return the SourceModule.expr representing an application of imageWidth
		 */
		public static final SourceModel.Expr imageWidth(SourceModel.Expr image) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.imageWidth), image});
		}

		/**
		 * Name binding for function: imageWidth.
		 * @see #imageWidth(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName imageWidth = 
			QualifiedName.make(CAL_Drawing.MODULE_NAME, "imageWidth");

		/**
		 * Helper binding method for function: jHitClip. 
		 * @param graphics
		 * @param arg_2
		 * @param arg_3
		 * @param arg_4
		 * @param arg_5
		 * @return the SourceModule.expr representing an application of jHitClip
		 */
		public static final SourceModel.Expr jHitClip(SourceModel.Expr graphics, SourceModel.Expr arg_2, SourceModel.Expr arg_3, SourceModel.Expr arg_4, SourceModel.Expr arg_5) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jHitClip), graphics, arg_2, arg_3, arg_4, arg_5});
		}

		/**
		 * @see #jHitClip(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param graphics
		 * @param arg_2
		 * @param arg_3
		 * @param arg_4
		 * @param arg_5
		 * @return the SourceModel.Expr representing an application of jHitClip
		 */
		public static final SourceModel.Expr jHitClip(SourceModel.Expr graphics, int arg_2, int arg_3, int arg_4, int arg_5) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jHitClip), graphics, SourceModel.Expr.makeIntValue(arg_2), SourceModel.Expr.makeIntValue(arg_3), SourceModel.Expr.makeIntValue(arg_4), SourceModel.Expr.makeIntValue(arg_5)});
		}

		/**
		 * Name binding for function: jHitClip.
		 * @see #jHitClip(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName jHitClip = 
			QualifiedName.make(CAL_Drawing.MODULE_NAME, "jHitClip");

		/**
		 * Helper binding method for function: jLoadResourceIcon. 
		 * @param iconResourceName
		 * @return the SourceModule.expr representing an application of jLoadResourceIcon
		 */
		public static final SourceModel.Expr jLoadResourceIcon(SourceModel.Expr iconResourceName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jLoadResourceIcon), iconResourceName});
		}

		/**
		 * @see #jLoadResourceIcon(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param iconResourceName
		 * @return the SourceModel.Expr representing an application of jLoadResourceIcon
		 */
		public static final SourceModel.Expr jLoadResourceIcon(java.lang.String iconResourceName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jLoadResourceIcon), SourceModel.Expr.makeStringValue(iconResourceName)});
		}

		/**
		 * Name binding for function: jLoadResourceIcon.
		 * @see #jLoadResourceIcon(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName jLoadResourceIcon = 
			QualifiedName.make(CAL_Drawing.MODULE_NAME, "jLoadResourceIcon");

		/**
		 * Helper binding method for function: jLoadResourceImage. 
		 * @param imageResourceName
		 * @return the SourceModule.expr representing an application of jLoadResourceImage
		 */
		public static final SourceModel.Expr jLoadResourceImage(SourceModel.Expr imageResourceName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jLoadResourceImage), imageResourceName});
		}

		/**
		 * @see #jLoadResourceImage(org.openquark.cal.compiler.SourceModel.Expr)
		 * @param imageResourceName
		 * @return the SourceModel.Expr representing an application of jLoadResourceImage
		 */
		public static final SourceModel.Expr jLoadResourceImage(java.lang.String imageResourceName) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.jLoadResourceImage), SourceModel.Expr.makeStringValue(imageResourceName)});
		}

		/**
		 * Name binding for function: jLoadResourceImage.
		 * @see #jLoadResourceImage(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName jLoadResourceImage = 
			QualifiedName.make(CAL_Drawing.MODULE_NAME, "jLoadResourceImage");

		/**
		 * Constructs a line shape.
		 * @param x1 (CAL type: <code>Cal.Core.Prelude.Double</code>)
		 * @param y1 (CAL type: <code>Cal.Core.Prelude.Double</code>)
		 * @param x2 (CAL type: <code>Cal.Core.Prelude.Double</code>)
		 * @param y2 (CAL type: <code>Cal.Core.Prelude.Double</code>)
		 * @return (CAL type: <code>Cal.Experimental.Graphics.Drawing.Shape</code>) 
		 */
		public static final SourceModel.Expr makeLine(SourceModel.Expr x1, SourceModel.Expr y1, SourceModel.Expr x2, SourceModel.Expr y2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeLine), x1, y1, x2, y2});
		}

		/**
		 * @see #makeLine(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param x1
		 * @param y1
		 * @param x2
		 * @param y2
		 * @return the SourceModel.Expr representing an application of makeLine
		 */
		public static final SourceModel.Expr makeLine(double x1, double y1, double x2, double y2) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeLine), SourceModel.Expr.makeDoubleValue(x1), SourceModel.Expr.makeDoubleValue(y1), SourceModel.Expr.makeDoubleValue(x2), SourceModel.Expr.makeDoubleValue(y2)});
		}

		/**
		 * Name binding for function: makeLine.
		 * @see #makeLine(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeLine = 
			QualifiedName.make(CAL_Drawing.MODULE_NAME, "makeLine");

		/**
		 * Constructs a polygon using the specified points.
		 * @param points (CAL type: <code>[(Cal.Core.Prelude.Double, Cal.Core.Prelude.Double)]</code>)
		 * @return (CAL type: <code>Cal.Experimental.Graphics.Drawing.Shape</code>) 
		 */
		public static final SourceModel.Expr makePolygon(SourceModel.Expr points) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makePolygon), points});
		}

		/**
		 * Name binding for function: makePolygon.
		 * @see #makePolygon(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makePolygon = 
			QualifiedName.make(CAL_Drawing.MODULE_NAME, "makePolygon");

		/**
		 * Constructs a rectangle shape.
		 * @param x (CAL type: <code>Cal.Core.Prelude.Double</code>)
		 * @param y (CAL type: <code>Cal.Core.Prelude.Double</code>)
		 * @param w (CAL type: <code>Cal.Core.Prelude.Double</code>)
		 * @param h (CAL type: <code>Cal.Core.Prelude.Double</code>)
		 * @return (CAL type: <code>Cal.Experimental.Graphics.Drawing.Shape</code>) 
		 */
		public static final SourceModel.Expr makeRectangle(SourceModel.Expr x, SourceModel.Expr y, SourceModel.Expr w, SourceModel.Expr h) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeRectangle), x, y, w, h});
		}

		/**
		 * @see #makeRectangle(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 * @param x
		 * @param y
		 * @param w
		 * @param h
		 * @return the SourceModel.Expr representing an application of makeRectangle
		 */
		public static final SourceModel.Expr makeRectangle(double x, double y, double w, double h) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.makeRectangle), SourceModel.Expr.makeDoubleValue(x), SourceModel.Expr.makeDoubleValue(y), SourceModel.Expr.makeDoubleValue(w), SourceModel.Expr.makeDoubleValue(h)});
		}

		/**
		 * Name binding for function: makeRectangle.
		 * @see #makeRectangle(org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr, org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName makeRectangle = 
			QualifiedName.make(CAL_Drawing.MODULE_NAME, "makeRectangle");

		/**
		 * Helper binding method for function: renderingHint_Antialiasing_Off. 
		 * @return the SourceModule.expr representing an application of renderingHint_Antialiasing_Off
		 */
		public static final SourceModel.Expr renderingHint_Antialiasing_Off() {
			return 
				SourceModel.Expr.Var.make(
					Functions.renderingHint_Antialiasing_Off);
		}

		/**
		 * Name binding for function: renderingHint_Antialiasing_Off.
		 * @see #renderingHint_Antialiasing_Off()
		 */
		public static final QualifiedName renderingHint_Antialiasing_Off = 
			QualifiedName.make(
				CAL_Drawing.MODULE_NAME, 
				"renderingHint_Antialiasing_Off");

		/**
		 * Helper binding method for function: renderingHint_Antialiasing_On. 
		 * @return the SourceModule.expr representing an application of renderingHint_Antialiasing_On
		 */
		public static final SourceModel.Expr renderingHint_Antialiasing_On() {
			return 
				SourceModel.Expr.Var.make(
					Functions.renderingHint_Antialiasing_On);
		}

		/**
		 * Name binding for function: renderingHint_Antialiasing_On.
		 * @see #renderingHint_Antialiasing_On()
		 */
		public static final QualifiedName renderingHint_Antialiasing_On = 
			QualifiedName.make(
				CAL_Drawing.MODULE_NAME, 
				"renderingHint_Antialiasing_On");

		/**
		 * Helper binding method for function: renderingHint_FractionalMetrics_Off. 
		 * @return the SourceModule.expr representing an application of renderingHint_FractionalMetrics_Off
		 */
		public static final SourceModel.Expr renderingHint_FractionalMetrics_Off() {
			return 
				SourceModel.Expr.Var.make(
					Functions.renderingHint_FractionalMetrics_Off);
		}

		/**
		 * Name binding for function: renderingHint_FractionalMetrics_Off.
		 * @see #renderingHint_FractionalMetrics_Off()
		 */
		public static final QualifiedName renderingHint_FractionalMetrics_Off = 
			QualifiedName.make(
				CAL_Drawing.MODULE_NAME, 
				"renderingHint_FractionalMetrics_Off");

		/**
		 * Helper binding method for function: renderingHint_FractionalMetrics_On. 
		 * @return the SourceModule.expr representing an application of renderingHint_FractionalMetrics_On
		 */
		public static final SourceModel.Expr renderingHint_FractionalMetrics_On() {
			return 
				SourceModel.Expr.Var.make(
					Functions.renderingHint_FractionalMetrics_On);
		}

		/**
		 * Name binding for function: renderingHint_FractionalMetrics_On.
		 * @see #renderingHint_FractionalMetrics_On()
		 */
		public static final QualifiedName renderingHint_FractionalMetrics_On = 
			QualifiedName.make(
				CAL_Drawing.MODULE_NAME, 
				"renderingHint_FractionalMetrics_On");

		/**
		 * Helper binding method for function: renderingHint_Rendering_Quality. 
		 * @return the SourceModule.expr representing an application of renderingHint_Rendering_Quality
		 */
		public static final SourceModel.Expr renderingHint_Rendering_Quality() {
			return 
				SourceModel.Expr.Var.make(
					Functions.renderingHint_Rendering_Quality);
		}

		/**
		 * Name binding for function: renderingHint_Rendering_Quality.
		 * @see #renderingHint_Rendering_Quality()
		 */
		public static final QualifiedName renderingHint_Rendering_Quality = 
			QualifiedName.make(
				CAL_Drawing.MODULE_NAME, 
				"renderingHint_Rendering_Quality");

		/**
		 * Helper binding method for function: renderingHint_Rendering_Speed. 
		 * @return the SourceModule.expr representing an application of renderingHint_Rendering_Speed
		 */
		public static final SourceModel.Expr renderingHint_Rendering_Speed() {
			return 
				SourceModel.Expr.Var.make(
					Functions.renderingHint_Rendering_Speed);
		}

		/**
		 * Name binding for function: renderingHint_Rendering_Speed.
		 * @see #renderingHint_Rendering_Speed()
		 */
		public static final QualifiedName renderingHint_Rendering_Speed = 
			QualifiedName.make(
				CAL_Drawing.MODULE_NAME, 
				"renderingHint_Rendering_Speed");

		/**
		 * Helper binding method for function: renderingHint_TextAntialiasing_Off. 
		 * @return the SourceModule.expr representing an application of renderingHint_TextAntialiasing_Off
		 */
		public static final SourceModel.Expr renderingHint_TextAntialiasing_Off() {
			return 
				SourceModel.Expr.Var.make(
					Functions.renderingHint_TextAntialiasing_Off);
		}

		/**
		 * Name binding for function: renderingHint_TextAntialiasing_Off.
		 * @see #renderingHint_TextAntialiasing_Off()
		 */
		public static final QualifiedName renderingHint_TextAntialiasing_Off = 
			QualifiedName.make(
				CAL_Drawing.MODULE_NAME, 
				"renderingHint_TextAntialiasing_Off");

		/**
		 * Helper binding method for function: renderingHint_TextAntialiasing_On. 
		 * @return the SourceModule.expr representing an application of renderingHint_TextAntialiasing_On
		 */
		public static final SourceModel.Expr renderingHint_TextAntialiasing_On() {
			return 
				SourceModel.Expr.Var.make(
					Functions.renderingHint_TextAntialiasing_On);
		}

		/**
		 * Name binding for function: renderingHint_TextAntialiasing_On.
		 * @see #renderingHint_TextAntialiasing_On()
		 */
		public static final QualifiedName renderingHint_TextAntialiasing_On = 
			QualifiedName.make(
				CAL_Drawing.MODULE_NAME, 
				"renderingHint_TextAntialiasing_On");

		/**
		 * Creates a paint for a solid colour.
		 * @param color (CAL type: <code>Cal.Graphics.Color.Color</code>)
		 * @return (CAL type: <code>Cal.Experimental.Graphics.Drawing.Paint</code>) 
		 */
		public static final SourceModel.Expr solidColourPaint(SourceModel.Expr color) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.solidColourPaint), color});
		}

		/**
		 * Name binding for function: solidColourPaint.
		 * @see #solidColourPaint(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName solidColourPaint = 
			QualifiedName.make(CAL_Drawing.MODULE_NAME, "solidColourPaint");

		/**
		 * A solid stroke with the specified width.
		 * @param width (CAL type: <code>Cal.Core.Prelude.Num a => a</code>)
		 * @return (CAL type: <code>Cal.Experimental.Graphics.Drawing.Stroke</code>) 
		 */
		public static final SourceModel.Expr solidStroke(SourceModel.Expr width) {
			return 
				SourceModel.Expr.Application.make(
					new SourceModel.Expr[] {SourceModel.Expr.Var.make(Functions.solidStroke), width});
		}

		/**
		 * Name binding for function: solidStroke.
		 * @see #solidStroke(org.openquark.cal.compiler.SourceModel.Expr)
		 */
		public static final QualifiedName solidStroke = 
			QualifiedName.make(CAL_Drawing.MODULE_NAME, "solidStroke");

	}
	/**
	 * A hash of the concatenated JavaDoc for this class (including inner classes).
	 * This value is used when checking for changes to generated binding classes.
	 */
	public static final int javaDocHash = 1448324154;

}
