import dts from 'rollup-plugin-dts'
import esbuild from 'rollup-plugin-esbuild'
import { uglify } from 'rollup-plugin-uglify';


import * as pack from './package.json' assert { type: 'json' };
const name = pack.default.main.replace(/\.js$/, '')

const bundle = config => ({
    ...config,
    input: 'src/escriba.ts',
    external: id => !/^[./]/.test(id),
})

export default [
    bundle({
        plugins: [esbuild(), uglify()],
        output: [
            {
                file: `${name}.js`,
                format: 'cjs',
                sourcemap: true,
            },
            {
                file: `${name}.mjs`,
                format: 'es',
                sourcemap: true,
            },
        ],
    }),
    bundle({
        plugins: [dts()],
        output: {
            file: `${name}.d.ts`,
            format: 'es',
        },
    }),
]
