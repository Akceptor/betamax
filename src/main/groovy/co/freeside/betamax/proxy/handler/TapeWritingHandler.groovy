package co.freeside.betamax.proxy.handler

import co.freeside.betamax.Recorder
import co.freeside.betamax.message.Request
import co.freeside.betamax.message.Response

import java.util.logging.Logger

import static java.net.HttpURLConnection.HTTP_FORBIDDEN
import static java.util.logging.Level.INFO

class TapeWritingHandler extends ChainedHttpHandler {

	private final Recorder recorder

	private final Logger log = Logger.getLogger(TapeWritingHandler.name)

	TapeWritingHandler(Recorder recorder) {
		this.recorder = recorder
	}

	Response handle(Request request) {
		def tape = recorder.getTape()
		if (!tape) {
			throw new ProxyException(HTTP_FORBIDDEN, 'No tape')
		} else if (!tape.writable) {
			throw new ProxyException(HTTP_FORBIDDEN, 'Tape is read-only')
		}

		def response = chain(request)
		log.log INFO, "Recording to '$tape.name'"
		tape.record(request, response)

		response.addHeader('X-Betamax', 'REC')

		response
	}

}
