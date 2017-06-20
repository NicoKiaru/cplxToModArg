/*
 * To the extent possible under law, the ImageJ developers have waived
 * all copyright and related or neighboring rights to this tutorial code.
 *
 * See the CC0 1.0 Universal license for details:
 *     http://creativecommons.org/publicdomain/zero/1.0/
 */

package eu.kiaru;

import net.imagej.Dataset;
import net.imagej.ImageJ;
import net.imagej.ops.OpService;
import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.img.cell.CellImgFactory;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.complex.ComplexFloatType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.ComplexType;

import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.ui.UIService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * This example illustrates how to create an ImageJ {@link Command} plugin.
 * <p>
 * The code here is a simple Gaussian blur using ImageJ Ops.
 * </p>
 * <p>
 * You should replace the parameter fields with your own inputs and outputs,
 * and replace the {@link run} method implementation with your own logic.
 * </p>
 */
@Plugin(type = Command.class, menuPath = "Plugins>CplxToModArg")
public class CplxToModArg<C extends ComplexType<C>>  implements Command { 

    @Parameter
    private Img<C> imgIn; 

    @Parameter(type = ItemIO.OUTPUT)
    private Img<FloatType> modOut;

    @Parameter(type = ItemIO.OUTPUT)
    private Img<FloatType> argOut;

    @Override
    public void run() {
    	// creating output image 1
    	try {
			modOut = imgIn.factory().imgFactory(new FloatType()).create( imgIn, new FloatType() );
			argOut = imgIn.factory().imgFactory(new FloatType()).create( imgIn, new FloatType() );
		} catch (IncompatibleTypeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    	
    	final Cursor<C> c_in = imgIn.cursor();
    	final Cursor< FloatType > c_modOut = modOut.localizingCursor();
    	final Cursor< FloatType > c_argOut = argOut.localizingCursor();
    	//int i=0;
    	while( c_in.hasNext() )
		{
    		c_in.fwd();
			c_modOut.fwd();
			c_argOut.fwd();
			i++;
			//if (i % 1000==0) {System.out.println(c_in.get());}
			//in.setPosition( out );// correct ; this can be as well in.setPostion(out.getPosition(0),0); and so on.
			c_modOut.get().set( c_in.get().getPowerFloat() );
			c_argOut.get().set( c_in.get().getPhaseFloat() );
		}
    }

    /**
     * This main function serves for development purposes.
     * It allows you to run the plugin immediately out of
     * your integrated development environment (IDE).
     *
     * @param args whatever, it's ignored
     * @throws Exception
     */
    public static void main(final String... args) throws Exception {
        // create the ImageJ application context with all available services
        final ImageJ ij = new ImageJ();
        ij.ui().showUI();

        // ask the user for a file to open
        final File file = ij.ui().chooseFile(null, "open");

        if (file != null) {
            // load the dataset
            final Dataset dataset = ij.scifio().datasetIO().open(file.getPath());
            
            // show the image
            ij.ui().show(dataset);
            RandomAccessibleInterval<ComplexFloatType> result = ij.op().filter().fft((RandomAccessibleInterval) dataset.getImgPlus().getImg());
            //final Dataset fftdataset = (Dataset) ij.op().run("filter.createfftoutput", dataset);
            
            //ij.ui().show(result);
            RandomAccess<ComplexFloatType> rA = result.randomAccess();
            rA.setPosition(new int[] {1,1});
            System.out.println(rA.get());
            
            // invoke the plugin
            ij.command().run(CplxToModArg.class, true, "imgIn", result);
        }
    }

}
