package info.openrocket.swing.gui.rocketfigure;

import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.Random;

import info.openrocket.core.rocketcomponent.MassComponent;
import info.openrocket.core.rocketcomponent.MassObject;
import info.openrocket.core.rocketcomponent.RocketComponent;
import info.openrocket.core.util.Coordinate;
import info.openrocket.core.util.MathUtil;
import info.openrocket.core.util.Transformation;


public class MassComponentShapes extends RocketComponentShapes {
	@Override
	public Class<? extends RocketComponent> getShapeClass() {
		return MassComponent.class;
	}

	@Override
	public RocketComponentShapes[] getShapesSide(final RocketComponent component, final Transformation transformation) {
		final MassComponent massObj = (MassComponent)component;
		
		final double length = massObj.getLength();
		final double radius = massObj.getRadius(); // radius of the object, itself
		// magic number, but it's only cosmetic -- it just has to look pretty
		final double arc = Math.min(length, 2*radius) * 0.7;
		final double radialDistance = massObj.getRadialPosition();
		final double radialAngleRadians = massObj.getRadialDirection();
		
		final Coordinate localPosition = new Coordinate(0,
														radialDistance * Math.cos(radialAngleRadians),
														radialDistance * Math.sin(radialAngleRadians));
		final Coordinate renderPosition = transformation.transform(localPosition);
		
		Shape[] s = {new RoundRectangle2D.Double(renderPosition.x, renderPosition.y - radius, length, 2*radius, arc, arc)};
		
		final MassComponent.MassComponentType type = ((MassComponent)component).getMassComponentType();
		switch (type) {
		case ALTIMETER:
			s = addAltimeterSymbol(s);
			break;
		case FLIGHTCOMPUTER:
			s = addFlightComputerSymbol(s);
			break;
		case DEPLOYMENTCHARGE:
			s = addDeploymentChargeSymbol(s);
			break;
		case RECOVERYHARDWARE:
			s = addRecoveryHardwareSymbol(s);
			break;
		case PAYLOAD:
			s = addPayloadSymbol(s);
			break;
		case TRACKER:
			s = addTrackerSymbol(s);
			break;
		case BATTERY:
			s = addBatterySymbol(s);
			break;
		case MASSCOMPONENT:
		}
		
		return RocketComponentShapes.toArray(s, component);
	}


	@Override
	public RocketComponentShapes[] getShapesBack(final RocketComponent component, final Transformation transformation) {
		final MassObject massObj = (MassObject)component;
		
		final double radius = massObj.getRadius(); // radius of the object, itself
		final double diameter = 2*radius;
		final double radialDistance = massObj.getRadialPosition();
		final double radialAngleRadians = massObj.getRadialDirection();
		
		final Coordinate localPosition = new Coordinate(0,
														radialDistance * Math.cos(radialAngleRadians),
														radialDistance * Math.sin(radialAngleRadians));
		final Coordinate renderPosition = transformation.transform(localPosition);
		
		final Shape[] s = {new Ellipse2D.Double(renderPosition.z - radius, renderPosition.y - radius, diameter, diameter)};
		
		return RocketComponentShapes.toArray(s, component);
	}
	
	private static Shape[] addAltimeterSymbol(Shape[] baseShape){
		int offset=baseShape.length;
		Shape[] newShape = new Shape[baseShape.length+1];
		System.arraycopy(baseShape, 0, newShape, 0, baseShape.length);
			
		Rectangle2D bounds = baseShape[0].getBounds2D();
		Double vMargin = bounds.getHeight()/8.0;
		Double hMargin = bounds.getWidth()/2.25;
		Double halfArrowWidth=MathUtil.min(hMargin, vMargin);
		
		Path2D.Double symbol = new Path2D.Double();
		symbol.moveTo(bounds.getCenterX(), bounds.getY()+vMargin);
		symbol.lineTo(bounds.getCenterX(), bounds.getY()+7*vMargin);
		symbol.lineTo(bounds.getCenterX()-halfArrowWidth, bounds.getY()+6*vMargin);
		symbol.lineTo(bounds.getCenterX()+halfArrowWidth, bounds.getY()+6*vMargin);
		symbol.lineTo(bounds.getCenterX(), bounds.getY()+7*vMargin);
		
		newShape[offset]= symbol;
		return newShape;
	}

	private static Shape[] addFlightComputerSymbol(Shape[] baseShape){
		int pins=12;
		int offset=baseShape.length;
		Shape[] newShape = new Shape[baseShape.length+1+pins];
		System.arraycopy(baseShape, 0, newShape, 0, baseShape.length);
		
		Rectangle2D bounds = baseShape[0].getBounds2D();

			
		Double vMargin = bounds.getHeight()/8.0;
		Double hMargin = bounds.getWidth()/6.0;
		Double pinHeight=vMargin;
		Double pinSpacing=(bounds.getWidth()-2*hMargin)/(pins+1);
		Double pinWidth=pinSpacing/2;
		newShape[offset]=new Rectangle2D.Double(bounds.getX()+hMargin, bounds.getY()+2*vMargin, 4*hMargin,4*vMargin);
		for(int i=0; i<(pins/2); i++){
			newShape[i+1+offset]=new Rectangle2D.Double(bounds.getX()+hMargin+2*i*pinSpacing+pinSpacing, bounds.getY()+6*vMargin, pinWidth, pinHeight);
			newShape[i+pins/2+1+offset]=new Rectangle2D.Double(bounds.getX()+hMargin+2*i*pinSpacing+pinSpacing, bounds.getY()+vMargin, pinWidth, pinHeight);
		}
		//newShape[1]=symbol;
		return newShape;
	}
	
	private static Shape[] addTrackerSymbol(Shape[] baseShape){
		Shape[] newShape = new Shape[baseShape.length+7];
		int offset=baseShape.length;
		System.arraycopy(baseShape, 0, newShape, 0, baseShape.length);
		
		Rectangle2D bounds = baseShape[0].getBounds2D();
		Double vMargin=bounds.getWidth()/10;
		
		Double xCenter=bounds.getCenterX();
		Double yCenter=bounds.getCenterY();
		
		Double arcExtent = 60.0;
		Double arcStart1 = 360-arcExtent/2;
		Double arcStart2 = 180-arcExtent/2;
		
		if(3*vMargin*Math.sin(Math.toRadians(arcExtent/2))>0.9*bounds.getHeight()/2){
			vMargin=0.9*bounds.getHeight()/(6*Math.sin(Math.toRadians(arcExtent/2)));
		}
		newShape[offset]= new Ellipse2D.Double(xCenter-vMargin/2, yCenter-vMargin/2,vMargin,vMargin);
		for(int i=1; i<4; i++){
			newShape[i+offset]= new Arc2D.Double(xCenter-i*vMargin, yCenter-i*vMargin, 2*i*vMargin, 2*i*vMargin, arcStart1,arcExtent,Arc2D.OPEN);		
			newShape[i+3+offset]= new Arc2D.Double(xCenter-i*vMargin, yCenter-i*vMargin, 2*i*vMargin, 2*i*vMargin, arcStart2,arcExtent,Arc2D.OPEN);					
		}
		return newShape;
	}
	private static Shape[] addPayloadSymbol(Shape[] baseShape){
		Shape[] newShape = new Shape[baseShape.length+1];
		int offset=baseShape.length;
		System.arraycopy(baseShape, 0, newShape, 0, baseShape.length);
		
		Rectangle2D bounds = baseShape[0].getBounds2D();
		Double vMargin=bounds.getHeight()/10;
		Double hMargin=bounds.getWidth()/10;
		
	
		newShape[offset]= new Ellipse2D.Double(bounds.getX()+hMargin, bounds.getY()+vMargin,bounds.getWidth()-2*hMargin,bounds.getHeight()-2*vMargin);
		
		return newShape;
	}
	private static Shape[] addRecoveryHardwareSymbol(Shape[] baseShape){
		Shape[] newShape = new Shape[baseShape.length+3];
		int offset=baseShape.length;
		System.arraycopy(baseShape, 0, newShape, 0, baseShape.length);
		Rectangle2D bounds = baseShape[0].getBounds2D();
		Double vMargin=bounds.getHeight()/8;
		Double hMargin=bounds.getWidth()/8;
		
	
		newShape[offset]= new RoundRectangle2D.Double(bounds.getX()+hMargin, bounds.getY()+vMargin,bounds.getWidth()-2*hMargin,bounds.getHeight()-2*vMargin, 15, 5);
		newShape[offset+1]= new RoundRectangle2D.Double(bounds.getX()+hMargin+vMargin, bounds.getY()+2*vMargin,bounds.getWidth()-2*hMargin-2*vMargin,bounds.getHeight()-4*vMargin, 15, 5);
		newShape[offset+2]= new Rectangle2D.Double(bounds.getCenterX()-1.5*hMargin, bounds.getCenterY()+1.5*vMargin, 3*hMargin, 2*vMargin);
		return newShape;
	}

	private static Shape[] addDeploymentChargeSymbol(Shape[] baseShape) {
		int rays = 15;
		Shape[] newShape = new Shape[baseShape.length + 2];
		int offset = baseShape.length;
		System.arraycopy(baseShape, 0, newShape, 0, baseShape.length);

		Rectangle2D bounds = baseShape[0].getBounds2D();
		double xCenter = bounds.getCenterX();
		double yCenter = bounds.getCenterY();
		Random rand = new Random();

		// Make arc size proportional to component size
		double arcRadius = Math.min(bounds.getWidth(), bounds.getHeight()) / 4.0;
		newShape[offset] = new Arc2D.Double(
				xCenter - arcRadius,
				yCenter - arcRadius,
				2 * arcRadius,
				2 * arcRadius,
				55.0,
				180.0,
				Arc2D.CHORD
		);

		Path2D.Double explosion = new Path2D.Double();
		newShape[offset + 1] = explosion;

		// Scale rays proportionally to component size
		double maxRayLength = Math.min(bounds.getWidth(), bounds.getHeight()) / 4.0;

		for (int i = 1; i < rays; i++) {
			double rx = rand.nextDouble() * maxRayLength;
			double ry = rand.nextDouble() * maxRayLength;
			explosion.moveTo(xCenter, yCenter);
			explosion.lineTo(xCenter + rx, yCenter + ry);
		}

		return newShape;
	}

	private static Shape[] addBatterySymbol(Shape[] baseShape){
		Shape[] newShape = new Shape[baseShape.length+1];
		Rectangle2D bounds = baseShape[0].getBounds2D();
		int offset=baseShape.length;
		System.arraycopy(baseShape, 0, newShape, 0, baseShape.length);
	
			
		Double vMargin = bounds.getHeight()/8.0;
		Double hMargin = bounds.getWidth()/3.0;
		Double cellWidth=hMargin/3.0;
		Double cellTop=bounds.getY()+7*vMargin;
		Double cellBottom=bounds.getY()+vMargin;
		
		
		Path2D.Double symbol = new Path2D.Double();
		symbol.moveTo(bounds.getX()+hMargin, bounds.getCenterY());
		symbol.lineTo(bounds.getX()+2*hMargin/3, bounds.getCenterY());
		for(Double x=bounds.getX()+hMargin; x<bounds.getX()+2*hMargin; x+=cellWidth){
			symbol.moveTo(x,cellTop);
			symbol.lineTo(x, cellBottom);
			symbol.moveTo(x+cellWidth/2.0, cellBottom+vMargin);
			symbol.lineTo(x+cellWidth/2.0, cellTop-vMargin);
            
       }
		symbol.moveTo(bounds.getX()+bounds.getWidth()-2*hMargin/3-cellWidth/2, bounds.getCenterY());
		symbol.lineTo(bounds.getX()+2*hMargin-cellWidth/2, bounds.getCenterY());

		newShape[offset]= symbol;
		return newShape;

	}

	
}
