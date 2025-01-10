/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ["../resources/templates/**/*.{html,js}"],
  theme: {
    extend: {
      colors: {
        primary: '#008ed6',
        secondary: '#0aadff',
        'tahiti': {
          light: '#67e8f9',
          DEFAULT: '#06b6d4',
          dark: '#0e7490',
        },
      },
    },
  },
  plugins: [],
}

