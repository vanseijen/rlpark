package rlpark.plugin.rltoys.junit.math.vector.testing;

import org.junit.Assert;
import org.junit.Test;

import rlpark.plugin.rltoys.math.vector.implementations.BVector;
import rlpark.plugin.rltoys.math.vector.implementations.PVector;
import rlpark.plugin.rltoys.math.vector.implementations.SVector;


public class BVectorTest {
  private final BVector a = BVector.toBVector(10, new int[] { 1, 2, 4, 5 });
  private final PVector b = new PVector(0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0);
  private final SVector c = VectorTest.newSVector(0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0);

  @Test
  public void testCopyClear() {
    VectorTest.assertEquals(new PVector(0.0, 1.0, 1.0, 0.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0), a.copy());
    BVector a2 = a.copy();
    a2.clear();
    VectorTest.assertEquals(BVector.toBVector(10, new int[] {}), a2);
  }

  @Test
  public void testRemove() {
    int[] indexes = a.activeIndexes().clone();
    BVector a2 = a.copy();
    for (int i : indexes) {
      a2.removeEntry(i);
      a2.removeEntry(i);
    }
    VectorTest.assertEquals(BVector.toBVector(10, new int[] {}), a2);
  }

  @Test
  public void testAdd() {
    PVector e = new PVector(0.1, 1.2, 1.3, 0.4, 1.5, 1.6, 0.7, 0.8, 0.9, 1.0);
    VectorTest.assertEquals(e, a.add(b));
    VectorTest.assertEquals(e, a.add(c));
  }

  @Test
  public void testSubtract() {
    PVector e = new PVector(-0.1, 0.8, 0.7, -0.4, 0.5, 0.4, -0.7, -0.8, -0.9, -1.0);
    VectorTest.assertEquals(e, a.subtract(b));
    VectorTest.assertEquals(e, a.subtract(c));
  }

  @Test
  public void testDotProduct() {
    Assert.assertEquals(0.2 + 0.3 + 0.5 + 0.6, a.dotProduct(b), .0);
    Assert.assertEquals(0.2 + 0.3 + 0.5 + 0.6, a.dotProduct(c), .0);
  }

  @Test
  public void testDimension() {
    Assert.assertEquals(10, a.getDimension());
  }

  @Test
  public void testMapMultiply() {
    PVector e = new PVector(0.0, 2.0, 2.0, 0.0, 2.0, 2.0, 0.0, 0.0, 0.0, 0.0);
    VectorTest.assertEquals(a.mapMultiply(2.0), e);
    e = new PVector(0.0, 4.0, 4.0, 0.0, 4.0, 4.0, 0.0, 0.0, 0.0, 0.0);
    VectorTest.assertEquals(a.mapMultiply(2.0).mapMultiply(2.0), e);
    VectorTest.assertEquals(a.mapMultiply(2.0).mapMultiplyToSelf(2.0), e);
  }

  @Test
  public void testMerge() {
    BVector b1 = BVector.toBVector(100, new int[] { 1, 5, 10 });
    BVector b2 = BVector.toBVector(100, new int[] { 20, 30 });
    b1.mergeSubVector(50, b2);
    VectorTest.assertEquals(BVector.toBVector(100, new int[] { 1, 5, 10, 70, 80 }), b1);
  }
}
