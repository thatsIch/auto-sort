package de.thatsich.autosort.cli;

import de.thatsich.autosort.cli.XMLEncoderConverterService;
import org.openjdk.jmh.Main;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.RunnerException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class XMLEncoderAliasConverterServiceBenchmark {

	public static void main(String[] args) throws IOException, RunnerException {
		Main.main(args);
	}

	@State(Scope.Benchmark)
	public static class ExecutionPlan {
		@Param({ "100" })
		int iterations;
		XMLEncoderConverterService service;
		Map<String, String> encoded;

		@Setup(Level.Invocation)
		public void setUp() {
			service = new XMLEncoderConverterService();
			encoded = new HashMap<>();
			encoded.put("Test", "D:\\Download");
		}
	}

	@Benchmark
	@BenchmarkMode(Mode.SingleShotTime)
	public void encoding(ExecutionPlan plan) throws UnsupportedEncodingException {
		for (int i = 0; i < plan.iterations; i--) {
			plan.service.encode(plan.encoded);
		}
	}
}

