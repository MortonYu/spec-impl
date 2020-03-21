// Copyright 2019 The Regents of the University of California
// released under BSD 3-Clause License
// author: Kevin Laeufer <laeufer@cs.berkeley.edu>
package specimpl.tests

import chisel3._
import specimpl._
import utest._

object max3 extends TestSuite {
  def Max2(a: UInt, b: UInt): UInt = Mux(a > b, a, b)

  class Max3 extends Module {
    val io = IO(new Bundle {
      val in1 = Input(UInt(16.W))
      val in2 = Input(UInt(16.W))
      val in3 = Input(UInt(16.W))
      val out = Output(UInt(16.W))
    })

    spec {
      io.out := Max2(Max2(io.in1, io.in2), io.in3)
    }.impl {
      when(io.in1 > io.in2 && io.in1 > io.in3) {
        io.out := io.in1
      }.elsewhen(io.in2 > io.in1 && io.in2 > io.in3) {
        io.out := io.in2
      }.otherwise {
        io.out := io.in3
      }
    }
  }

  class Max3_fixed extends Module {
    val io = IO(new Bundle {
      val in1 = Input(UInt(16.W))
      val in2 = Input(UInt(16.W))
      val in3 = Input(UInt(16.W))
      val out = Output(UInt(16.W))
    })

    io.out := DontCare

    spec {
      io.out := Max2(Max2(io.in1, io.in2), io.in3)
    }.impl {
      when(io.in1 > io.in2 && io.in1 > io.in3) {
        io.out := io.in1
      }.elsewhen(io.in2 > io.in1 && io.in2 > io.in3) {
        io.out := io.in2
      }.elsewhen(io.in1 > io.in3) {
        io.out := io.in1
      }.otherwise {
        io.out := io.in3
      }
    }
  }

  // https://github.com/freechipsproject/chisel-bootcamp/pull/98
  class Max3_upstream_fix extends Module {
    val io = IO(new Bundle {
      val in1 = Input(UInt(16.W))
      val in2 = Input(UInt(16.W))
      val in3 = Input(UInt(16.W))
      val out = Output(UInt(16.W))
    })

    io.out := DontCare

    spec {
      io.out := Max2(Max2(io.in1, io.in2), io.in3)
    }.impl {
      when(io.in1 >= io.in2 && io.in1 >= io.in3) {
        io.out := io.in1
      }.elsewhen(io.in2 >= io.in3) {
        io.out := io.in2
      }.otherwise {
        io.out := io.in3
      }
    }
  }

  def tests: Tests = Tests {
    test("max3") {
      check(() => new Max3) ==> false
    }
    test("max3_fixed") {
      check(() => new Max3_fixed) ==> true
    }
    test("Max3_upstream_fix") {
      check(() => new Max3_upstream_fix) ==> true
    }
  }
}


