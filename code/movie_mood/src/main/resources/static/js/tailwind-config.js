tailwind.config = {
    darkMode: "class",
    theme: {
        extend: {
            colors: {
                "inverse-on-surface": "#30312e",
                "inverse-primary": "#6b5c45",
                "on-secondary-fixed": "#1b1c19",
                "on-surface-variant": "#cfc5b9",
                "primary-fixed": "#f5dfc2",
                "on-error": "#690005",
                "on-surface": "#e4e2de",
                "surface-dim": "#131412",
                "surface-container-highest": "#343533",
                "background": "#131412",
                "surface-variant": "#343533",
                "on-primary-container": "#483b26",
                "surface-container-high": "#292a28",
                "secondary-container": "#464743",
                "on-tertiary": "#5b1a16",
                "on-primary-fixed": "#241a08",
                "surface": "#131412",
                "error-container": "#93000a",
                "on-tertiary-container": "#6b2621",
                "outline-variant": "#4c463d",
                "error": "#ffb4ab",
                "on-primary": "#3b2e1a",
                "primary-container": "#b8a58a",
                "on-tertiary-fixed": "#3e0504",
                "tertiary": "#ffb4ab",
                "outline": "#988f85",
                "surface-bright": "#393937",
                "on-tertiary-fixed-variant": "#78302a",
                "surface-container": "#1f201e",
                "inverse-surface": "#e4e2de",
                "on-secondary-fixed-variant": "#464743",
                "primary": "#d8c4a7",
                "secondary": "#c7c6c1",
                "secondary-fixed": "#e4e2dd",
                "primary-fixed-dim": "#d8c4a7",
                "surface-tint": "#d8c4a7",
                "on-background": "#e4e2de",
                "on-primary-fixed-variant": "#53452f",
                "on-error-container": "#ffdad6",
                "tertiary-fixed-dim": "#ffb4ab",
                "tertiary-fixed": "#ffdad6",
                "surface-container-lowest": "#0d0e0d",
                "on-secondary": "#30312d",
                "secondary-fixed-dim": "#c7c6c1",
                "on-secondary-container": "#b6b5b0",
                "surface-container-low": "#1b1c1a",
                "tertiary-container": "#ee8d83"
            },
            borderRadius: {
                DEFAULT: "0.125rem",
                lg: "0.25rem",
                xl: "0.5rem",
                full: "0.75rem"
            },
            spacing: {
                gutter: "1.5rem",
                "space-md": "1rem",
                margin: "3rem",
                "space-lg": "1.5rem",
                "space-2xl": "4rem",
                "space-xs": "0.5rem",
                "space-2xs": "0.25rem",
                "space-xl": "2.5rem",
                "margin-mobile": "1rem",
                "gutter-mobile": "0.75rem",
                "space-sm": "0.75rem"
            },
            fontFamily: {
                "outfit": ["Outfit", "sans-serif"],
                "manrope": ["Manrope", "sans-serif"],
                "body-sm": ["Outfit"],
                "body-md": ["Outfit"],
                "display-lg": ["Outfit"],
                "display-lg-mobile": ["Outfit"],
                "headline-sm": ["Outfit"],
                "label-eyebrow": ["Outfit"],
                "headline-lg": ["Outfit"],
                "body-lg": ["Outfit"],
                "headline-xl": ["Outfit"],
                "headline-xl-mobile": ["Outfit"],
                "label-catalog": ["Outfit"]
            },
            fontSize: {
                "body-sm": ["13px", {
                    lineHeight: "20px",
                    letterSpacing: "0.02em",
                    fontWeight: "500"
                }],
                "body-md": ["15px", {
                    lineHeight: "24px",
                    letterSpacing: "0.01em",
                    fontWeight: "400"
                }],
                "display-lg": ["56px", {
                    lineHeight: "64px",
                    letterSpacing: "-0.02em",
                    fontWeight: "300"
                }],
                "display-lg-mobile": ["36px", {
                    lineHeight: "44px",
                    letterSpacing: "-0.01em",
                    fontWeight: "300"
                }],
                "headline-sm": ["20px", {
                    lineHeight: "28px",
                    letterSpacing: "0.01em",
                    fontWeight: "500"
                }],
                "label-eyebrow": ["11px", {
                    lineHeight: "16px",
                    letterSpacing: "0.14em",
                    fontWeight: "500"
                }],
                "headline-lg": ["28px", {
                    lineHeight: "36px",
                    letterSpacing: "0em",
                    fontWeight: "400"
                }],
                "body-lg": ["18px", {
                    lineHeight: "28px",
                    letterSpacing: "0.01em",
                    fontWeight: "400"
                }],
                "headline-xl": ["40px", {
                    lineHeight: "48px",
                    letterSpacing: "-0.015em",
                    fontWeight: "400"
                }],
                "headline-xl-mobile": ["28px", {
                    lineHeight: "36px",
                    letterSpacing: "-0.01em",
                    fontWeight: "400"
                }],
                "label-catalog": ["11px", {
                    lineHeight: "14px",
                    letterSpacing: "0.08em",
                    fontWeight: "600"
                }]
            }
        }
    }
};