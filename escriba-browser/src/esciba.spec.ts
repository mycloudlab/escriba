import { Logger, Escriba } from './escriba';

describe("initialize logger", () => {
    test("deveria inicializar o projeto", async () => {
        let logger: Logger = await Escriba.init({});
        logger.info("");
    })
}) 