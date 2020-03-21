// Copyright 2019 The Regents of the University of California
// released under BSD 3-Clause License
// author: Kevin Laeufer <laeufer@cs.berkeley.edu>

import chisel3.RawModule
import chisel3.experimental.RunFirrtlTransform
import firrtl.{ChirrtlForm, CircuitState, MiddleFirrtlCompiler, Parser, Transform}

package object specimpl {
  def check[T <: RawModule](gen: () => T) = {
    val ir = chisel3.Driver.elaborate(gen)
    val fir = Parser.parseString(chisel3.Driver.emit(ir), Parser.UseInfo)
    val transforms = ir.annotations
      .collect { case anno: RunFirrtlTransform => anno.transformClass }
      .distinct
      .filterNot(_ == classOf[Transform])
      .map { transformClass: Class[_ <: Transform] =>
        transformClass.newInstance()
      }
    val compiler = new MiddleFirrtlCompiler()
    val annos = ir.annotations.map(_.toFirrtl)
    try {
      compiler.compile(CircuitState(fir, ChirrtlForm, annos), transforms)
      true
    } catch {
      // @todo cannot catch [[NotEquivalentException]]
      case _: firrtl.CustomTransformException => false
    }
  }
}
