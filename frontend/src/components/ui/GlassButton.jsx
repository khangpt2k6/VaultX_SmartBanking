import React from "react";

/**
 * Reusable Glass Button Component
 * Provides glassmorphic button with consistent styling
 */
export const GlassButton = ({
  children,
  variant = "default",
  size = "md",
  className = "",
  icon: Icon,
  iconPosition = "left",
  disabled = false,
  ...props
}) => {
  const sizeClasses = {
    sm: "px-3 py-1 text-sm",
    md: "px-6 py-2 text-base",
    lg: "px-8 py-3 text-lg",
  };

  const variantClasses = {
    default: "btn-glass",
    primary: "btn-glass btn-glass-primary",
  };

  const buttonClass = `
    ${variantClasses[variant] || variantClasses.default}
    ${sizeClasses[size] || sizeClasses.md}
    ${disabled ? "opacity-50 cursor-not-allowed" : "cursor-pointer"}
    ${className}
  `;

  return (
    <button className={buttonClass} disabled={disabled} {...props}>
      {Icon && iconPosition === "left" && <Icon size={18} />}
      {children}
      {Icon && iconPosition === "right" && <Icon size={18} />}
    </button>
  );
};

/**
 * Link Button Component
 * Glass button that functions as a link
 */
export const GlassLinkButton = ({
  children,
  to,
  variant = "default",
  size = "md",
  className = "",
  icon: Icon,
  iconPosition = "left",
  ...props
}) => {
  const sizeClasses = {
    sm: "px-3 py-1 text-sm",
    md: "px-6 py-2 text-base",
    lg: "px-8 py-3 text-lg",
  };

  const variantClasses = {
    default: "btn-glass",
    primary: "btn-glass btn-glass-primary",
  };

  const buttonClass = `
    ${variantClasses[variant] || variantClasses.default}
    ${sizeClasses[size] || sizeClasses.md}
    no-underline
    ${className}
  `;

  return (
    <a href={to} className={buttonClass} {...props}>
      {Icon && iconPosition === "left" && <Icon size={18} />}
      {children}
      {Icon && iconPosition === "right" && <Icon size={18} />}
    </a>
  );
};

export default GlassButton;
