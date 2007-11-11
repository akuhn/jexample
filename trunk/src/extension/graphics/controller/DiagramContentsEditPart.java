package extension.graphics.controller;

import java.util.List;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

public class DiagramContentsEditPart extends AbstractGraphicalEditPart {
    protected IFigure createFigure() {
        Figure f = new Figure();
        f.setOpaque(true);
        f.setLayoutManager(new XYLayout());
        return f;
    }

    protected void createEditPolicies() {
    }

    protected List getModelChildren() {
        return ((MyModelType)getModel()).getDiagramChildren();
    }
}
